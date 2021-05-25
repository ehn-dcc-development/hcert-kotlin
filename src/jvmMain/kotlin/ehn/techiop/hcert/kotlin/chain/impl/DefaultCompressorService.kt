package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CompressorService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import java.util.zip.Deflater
import java.util.zip.DeflaterInputStream
import java.util.zip.InflaterInputStream

/**
 * Compresses/decompresses input with ZLIB, [level] specifies the compression level (0-9)
 */
actual open class DefaultCompressorService actual constructor(private val level: Int) : CompressorService {

    /**
     * Compresses input with ZLIB = deflating
     */
    override fun encode(input: ByteArray): ByteArray {
        return DeflaterInputStream(input.inputStream(), Deflater(level)).readBytes()
    }

    /**
     * Decompresses input with ZLIB = inflating.
     */
    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.zlibDecoded = false
        return try {
            InflaterInputStream(input.inputStream()).readBytes().also {
                verificationResult.zlibDecoded = true
            }
        } catch (e: Throwable) {
            throw e
        }
    }

}
