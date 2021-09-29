package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CompressorService
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import kotlin.jvm.JvmOverloads

/**
 * Compresses/decompresses input with ZLIB, [level] specifies the compression level (0-9)
 */
open class DefaultCompressorService @JvmOverloads constructor(private val level: Int = 9) : CompressorService {

    private val adapter = CompressorAdapter()

    /**
     * Compresses input with ZLIB = deflating
     */
    override fun encode(input: ByteArray): ByteArray {
        return adapter.encode(input, level)
    }

    /**
     * Decompresses input with ZLIB = inflating.
     */
    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        try {
            return adapter.decode(input)
        } catch (e: Throwable) {
            throw VerificationException(Error.DECOMPRESSION_FAILED, cause = e)
        }
    }

}
