package ehn.techiop.hcert.kotlin.chain

/**
 * Encodes/decodes input as a CBOR structure
 */
interface CborService {

    fun encode(input: Eudgc): ByteArray

    fun decode(input: ByteArray, verificationResult: VerificationResult): GreenCertificate

}