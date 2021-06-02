package ehn.techiop.hcert.kotlin.chain

/**
 * Compresses/decompresses input
 */
interface CompressorService {

    fun encode(input: ByteArray): ByteArray

    fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray

}