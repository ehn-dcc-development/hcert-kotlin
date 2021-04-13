package ehn.techiop.hcert.kotlin.chain

interface CoseService {

    fun encode(input: ByteArray): ByteArray

    fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray

}