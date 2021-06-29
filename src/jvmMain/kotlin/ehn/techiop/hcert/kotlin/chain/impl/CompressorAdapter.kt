package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.impl.CompressionConstants.MAX_DECOMPRESSED_SIZE
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.zip.Deflater
import java.util.zip.DeflaterInputStream
import java.util.zip.InflaterInputStream

actual open class CompressorAdapter {

    actual fun encode(input: ByteArray, level: Int) =
        DeflaterInputStream(input.inputStream(), Deflater(level)).readBytes()

    actual fun decode(input: ByteArray) =
        InflaterInputStream(input.inputStream()).readBytes().also {
            val inflaterStream = InflaterInputStream(input.inputStream())
            val outputStream = ByteArrayOutputStream(DEFAULT_BUFFER_SIZE)
            inflaterStream.copyTo(outputStream)
            outputStream.toByteArray()
        }

}

// Adapted from kotlin-stdblib's kotlin.io.IOStreams.kt
private fun InflaterInputStream.copyTo(out: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        bytes = read(buffer)
        // begin patch
        if (bytesCopied > MAX_DECOMPRESSED_SIZE) {
            throw IllegalArgumentException("Decompression exceeded $MAX_DECOMPRESSED_SIZE bytes, is: $bytesCopied! Input must be invalid.")
        }
        // end patch
    }
    return bytesCopied
}