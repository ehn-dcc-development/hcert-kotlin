package ehn.techiop.hcert.kotlin.chain

/**
 * Wraps input in a COSE structure
 */
interface CoseService {

    fun encode(input: ByteArray): ByteArray

    fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray

}