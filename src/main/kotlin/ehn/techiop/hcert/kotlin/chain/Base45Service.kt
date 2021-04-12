package ehn.techiop.hcert.kotlin.chain

class Base45Service {

    private val encoder = Base45Encoder()

    fun encode(input: ByteArray) =
        encoder.encode(input)

    fun decode(input: String, verificationResult: VerificationResult): ByteArray {
        return try {
            encoder.decode(input).also {
                verificationResult.base45Decoded = true
            }
        } catch (e: Throwable) {
            input.toByteArray().also {
                verificationResult.base45Decoded = false
            }
        }
    }

}