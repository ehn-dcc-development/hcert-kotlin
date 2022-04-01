package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.*
import io.github.aakira.napier.Napier
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
            return adapter.decode(input).also {
                Napier.d(
                    """
                Data is decompressable
                Base64: ${it.asBase64()}
                Hex: ${it.toHexString()}
            """.trimIndent()
                )
            }
        } catch (e: Throwable) {
            throw VerificationException(Error.DECOMPRESSION_FAILED, cause = e)
        }
    }

}
