package ehn.techiop.hcert.kotlin.chain

import java.time.Instant


class CborProcessingChain(
    private val cborService: CborService,
    private val coseService: CoseService,
    private val contextIdentifierService: ContextIdentifierService,
    private val compressorService: CompressorService,
    private val base45Service: Base45Service
) {

    fun process(input: VaccinationData): ResultCbor {
        val cbor = cborService.encode(input)
        val cose = coseService.encode(cbor)
        val comCose = compressorService.encode(cose)
        val encodedComCose = base45Service.encode(comCose)
        val prefEncodedComCose = contextIdentifierService.encode(encodedComCose)
        return ResultCbor(cbor, cose, comCose, prefEncodedComCose)
    }

    fun verify(input: String, verificationResult: VerificationResult = VerificationResult()): VaccinationData {
        val plainInput = contextIdentifierService.decode(input, verificationResult)
        val compressedCose = base45Service.decode(plainInput, verificationResult)
        val cose = compressorService.decode(compressedCose, verificationResult)
        val cbor = coseService.decode(cose, verificationResult)
        return cborService.decode(cbor, verificationResult)
    }

}

class VerificationResult {
    /**
     * `exp` claim SHALL hold a timestamp.
     * Verifier MUST reject the payload after expiration.
     * It MUST not exceed the validity period of the DSC.
     */
    var expirationTime: Instant? = null

    /**
     * `iat` claim SHALL hold a timestamp. It MUST not predate the validity period of the DSC.
     */
    var issuedAt: Instant? = null

    /**
     * `iss` claim MAY hold ISO 3166-1 alpha-2 country code
     */
    var issuer: String? = null

    /**
     * The compressed CWT is encoded as ASCII using Base45
     */
    var base45Decoded = false

    /**
     * `HC1` SHALL be used as a prefix in the Base45 encoded data
     */
    var contextIdentifier: String? = null

    /**
     * CWT SHALL be compressed using ZLIB
     */
    var zlibDecoded = false

    /**
     * COSE signature MUST be verifiable
     */
    var coseVerified = false

    /**
     * The payload is structured and encoded as a CBOR with a COSE digital signature.
     */
    var cborDecoded = false
}

data class ResultCbor(
    val cbor: ByteArray,
    val cose: ByteArray,
    val compressedCose: ByteArray,
    val prefixedEncodedCompressedCose: String
)
