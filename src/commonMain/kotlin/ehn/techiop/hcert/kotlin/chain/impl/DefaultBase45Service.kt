package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.Base45Service
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult

/**
 * Encodes/decodes input in/from Base45
 */
open class DefaultBase45Service : Base45Service {

    private val encoder = Base45Encoder

    override fun encode(input: ByteArray) =
        encoder.encode(input)

    override fun decode(input: String, verificationResult: VerificationResult): ByteArray {
        try {
            return encoder.decode(input)
        } catch (e: Throwable) {
            throw VerificationException(Error.BASE_45_DECODING_FAILED, cause = e)
        }
    }

}
