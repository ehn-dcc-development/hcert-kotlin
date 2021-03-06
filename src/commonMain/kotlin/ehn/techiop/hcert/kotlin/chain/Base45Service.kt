package ehn.techiop.hcert.kotlin.chain

/**
 * Encodes/decodes input in/from Base45
 */
interface Base45Service {

    fun encode(input: ByteArray): String

    fun decode(input: String, verificationResult: VerificationResult): ByteArray

}