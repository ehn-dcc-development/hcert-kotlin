package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.log.antilog
import ehn.techiop.hcert.kotlin.log.globalLogLevel
import io.github.aakira.napier.Napier
import kotlin.js.JsName

/**
 * Main entry point for the creation/encoding and verification/decoding of HCERT data into QR codes
 *
 * @see [GreenCertificate]
 */
class Chain(
    private val cborService: CborService,
    private val cwtService: CwtService,
    private val coseService: CoseService,
    private val contextIdentifierService: ContextIdentifierService,
    private val compressorService: CompressorService,
    private val base45Service: Base45Service,
    private val schemaValidationService: SchemaValidationService
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
        var cbor: ByteArray? = null
        var cwt: ByteArray? = null
        var cose: ByteArray? = null
        var compressed: ByteArray? = null
        var encoded: String? = null

        try {
            encoded = contextIdentifierService.decode(input, verificationResult)
            compressed = base45Service.decode(encoded, verificationResult)
            cose = compressorService.decode(compressed, verificationResult)
            cwt = coseService.decode(cose, verificationResult)
            cbor = cwtService.decode(cwt, verificationResult)
            schemaValidationService.validate(cbor, verificationResult)
            eudgc = cborService.decode(cbor, verificationResult)
        } catch (t: Throwable) {
            Napier.w(
                message = t.message ?: "Decode Chain error",
                throwable = if (globalLogLevel == Napier.Level.VERBOSE) t else null,
                tag = logTag
            )
        }

        val chainDecodeResult = ChainDecodeResult(eudgc, cbor, cwt, cose, compressed, encoded)
        return DecodeResult(verificationResult, chainDecodeResult)
    }
}

