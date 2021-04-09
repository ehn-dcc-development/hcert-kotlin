package ehn.techiop.hcert.kotlin.chain

import java.util.zip.Deflater
import java.util.zip.Inflater

class CompressorService {

    /**
     * Compresses input with ZLIB = deflating
     */
    fun encode(input: ByteArray): ByteArray {
        val result = ByteArray(input.size * 4)
        val compressor = Deflater().apply {
            setInput(input)
            finish()
            deflate(result)
        }
        return result.copyOf(compressor.bytesWritten.toInt())
    }

    /**
     * Decompresses input with ZLIB = inflating
     */
    fun decode(input: ByteArray): ByteArray {
        val result = ByteArray(input.size * 4)
        val decompressor = Inflater().apply {
            setInput(input)
            inflate(result)
        }
        return result.copyOf(decompressor.bytesWritten.toInt())
    }

}