package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.data.GreenCertificate
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.js.JsName


//@JsExport
@Serializable
data class DecodeExtendedResult(val verificationResult: VerificationResult, val chainDecodeResult: ChainDecodeResult) {
    fun toJson() = Json.encodeToString(this)
}

//@JsExport
@Serializable
data class DecodeResult(val verificationResult: VerificationResult, val greenCertificate: GreenCertificate?) {
    fun toJson() = Json.encodeToString(this)
}

/**
 * Main entry point for the creation/encoding and verification/decoding of HCERT data into QR codes
 *
 * @see [Eudgc]
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
     * The result ([GreenCertificate]) will contain the parsed data.
     *
     * Beware that [verificationResult] will be filled with detailed information about the decoding,
     * which shall be passed to an instance of [DecisionService] to decide on a final verdict.
     */
    @JsName("decode")
    fun decode(input: String): DecodeResult {
        val decodeExtended = decodeExtended(input)
        return DecodeResult(decodeExtended.verificationResult, decodeExtended.chainDecodeResult.eudgc)
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
     *
     * Beware that [verificationResult] will be filled with detailed information about the decoding,
     * which shall be passed to an instance of [DecisionService] to decide on a final verdict.
     */
    @JsName("decodeExtended")
    fun decodeExtended(input: String): DecodeExtendedResult {
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

        }
        return DecodeExtendedResult(verificationResult, ChainDecodeResult(eudgc, cbor, cwt, cose, compressed, encoded))
    }
}

