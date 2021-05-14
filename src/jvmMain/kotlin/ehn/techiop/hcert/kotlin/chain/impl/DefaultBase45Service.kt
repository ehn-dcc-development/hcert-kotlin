package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.Base45Service
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.common.Base45Encoder

/**
 * Encodes/decodes input in/from Base45
 */
open class DefaultBase45Service : Base45Service {

    private val encoder = Base45Encoder

    override fun encode(input: ByteArray) =
        encoder.encode(input)

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