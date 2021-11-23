package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.data.CborObject
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.log.globalLogLevel
import ehn.techiop.hcert.kotlin.trust.CoseAdapter
import ehn.techiop.hcert.kotlin.trust.CwtHelper
import io.github.aakira.napier.Napier
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlin.js.JsName

/**
 * Main entry point for the creation/encoding and verification/decoding of HCERT data into QR codes
 *
 * @see [GreenCertificate]
 */
class Chain(
    private val higherOrderValidationService: HigherOrderValidationService,
    private val schemaValidationService: SchemaValidationService,
    private val cborService: CborService,
    private val cwtService: CwtService,
    private val coseService: CoseService,
    private val compressorService: CompressorService,
    private val base45Service: Base45Service,
    private val contextIdentifierService: ContextIdentifierService
) {
    private val logTag = "Chain${hashCode()}"

    /**
     * Process the [input], apply encoding in this order:
     * - [CborService]
     * - [CwtService]
     * - [CoseService]
     * - [CompressorService]
     * - [Base45Service]
     * - [ContextIdentifierService]
     *
     * The result ([ChainResult]) will contain all intermediate steps, as well as the final result in [ChainResult.step5Prefixed].
     */
    @JsName("encode")
    fun encode(input: GreenCertificate): ChainResult {
        val cbor = cborService.encode(input)
        val cwt = cwtService.encode(cbor)
        val cose = coseService.encode(cwt)
        val compressed = compressorService.encode(cose)
        val encoded = base45Service.encode(compressed)
        val prefixedEncoded = contextIdentifierService.encode(encoded)
        return ChainResult(cbor, cwt, cose, compressed, encoded, prefixedEncoded)
    }

    /**
     * Process the [input], apply decoding in this order:
     * - [ContextIdentifierService]
     * - [Base45Service]
     * - [CompressorService]
     * - [CoseService]
     * - [CwtService]
     * - [CborService]
     * - [SchemaValidationService]
     * The result ([ChainDecodeResult]) will contain the parsed data, as well as intermediate results.
     */
    @JsName("decode")
    fun decode(input: String): DecodeResult {
        val verificationResult = VerificationResult()

        var eudgc: GreenCertificate? = null
        var rawEuGcc: String? = null
        var cwt: ByteArray? = null
        var cose: ByteArray? = null
        var compressed: ByteArray? = null
        var encoded: String? = null
        var errors = mutableListOf<Error>()

        try {
            encoded = verificationResult.let { it.withRecovery(errors) { contextIdentifierService.decode(input, it) } }
            compressed = verificationResult.let { it.withRecovery(errors) { base45Service.decode(encoded, it) } }
            Napier.d(
                """
                QR code payload without prefix:
                Base64:
                ${compressed.asBase64()}
                Hex:
                ${compressed.toHexString()}
            """.trimIndent()
            )
            cose = verificationResult.let { it.withRecovery(errors) { compressorService.decode(compressed, it) } }
            Napier.d("COSE structure: ${CwtHelper.fromCbor(cose)}")
            cwt = verificationResult.let { it.withRecovery(errors) { coseService.decode(cose, it) } }

           // Napier.d("CWT structure: ${CwtHelper.fromCbor(cwt).toCborObject().toJsonString()}")
            val cborObj = verificationResult.let { it.withRecovery(errors) { cwtService.decode(cwt, it) } }
            rawEuGcc = cborObj.toJsonString()


            val schemaValidated =
                verificationResult.let { it.withRecovery(errors) { schemaValidationService.validate(cborObj, it) } }
            eudgc = verificationResult.let {
                it.withRecovery(errors) {
                    higherOrderValidationService.validate(
                        schemaValidated,
                        it
                    )
                }
            }
            //TODO print certificate candidates based on KID if verification fails
            verificationResult.encodedCertificate?.let {
                Napier.d(
                    """Signature Certificate Info
                    DER:
                    ${it.toHexString()}
                    Base64:
                    ${it.asBase64()}
                    Certificate Description: ${CertificateAdapter(it).prettyPrint()}
                """.trimMargin()
                )
            }
        } catch (e: VerificationException) {
            verificationResult.addError(e)
            Napier.w(
                message = e.message ?: "Decode Chain error",
                throwable = if (globalLogLevel == Napier.Level.VERBOSE) e else null,
                tag = logTag
            )
        }

        val chainDecodeResult = ChainDecodeResult(errors, eudgc, rawEuGcc, cwt, cose, compressed, encoded)
        return DecodeResult(verificationResult, chainDecodeResult)
    }

    private inline fun <reified T> VerificationResult.withRecovery(
        aggregatedErrors: MutableList<Error>,
        verify: () -> T
    ): T {
        return try {
            verify()
        } catch (e: Throwable) {
            if (e is NonFatalVerificationException) {
                addError(e)
                aggregatedErrors += e.error
                e.result as T
            } else throw e
        }
    }

    private fun VerificationResult.addError(e: VerificationException) {
        error = e.error
        e.details?.forEach { (k, v) -> errorDetails[k] = v }
    }
}