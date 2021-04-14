package ehn.techiop.hcert.kotlin.chain

interface CompressorService {

    fun encode(input: ByteArray): ByteArray

    fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray

}