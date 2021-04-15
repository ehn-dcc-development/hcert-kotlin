package ehn.techiop.hcert.kotlin.chain.faults

import ehn.techiop.hcert.kotlin.chain.Base45Service
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.impl.Base45Encoder

class FaultyBase45Service : Base45Service {

    private val encoder = Base45Encoder()

    override fun encode(input: ByteArray) =
        encoder.encode(input).dropLast(5) + "====="

    override fun decode(input: String, verificationResult: VerificationResult): ByteArray {
        verificationResult.base45Decoded = false
        return try {
            encoder.decode(input).also {
                verificationResult.base45Decoded = true
            }
        } catch (e: Throwable) {
            input.toByteArray()
        }
    }

}