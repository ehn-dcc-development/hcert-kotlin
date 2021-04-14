package ehn.techiop.hcert.kotlin.chain

interface Base45Service {

    fun encode(input: ByteArray): String

    fun decode(input: String, verificationResult: VerificationResult): ByteArray

}