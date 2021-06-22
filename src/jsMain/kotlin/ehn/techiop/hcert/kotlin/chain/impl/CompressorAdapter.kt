package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.NonNullableTryCatch.catch
import ehn.techiop.hcert.kotlin.chain.NonNullableTryCatch.jsTry
import ehn.techiop.hcert.kotlin.chain.impl.CompressionConstants.MAX_DECOMPRESSED_SIZE
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array
import ehn.techiop.hcert.kotlin.log.formatMag
import io.github.aakira.napier.Napier
import org.khronos.webgl.Uint8Array

actual class CompressorAdapter {
    val tag = "ZLib${hashCode()}"
    actual fun encode(input: ByteArray, level: Int): ByteArray {
        Napier.v("Deflating ${input.size.formatMag()}B input using compression level $level", tag = tag)
        val compressed = (Pako.deflate(input.toUint8Array(),
            object : Pako.DeflateFunctionOptions {
                override var level: dynamic = level
            }) as Uint8Array).toByteArray()
        Napier.v(
            "${input.size.formatMag()}B input using compression level $level was deflated to ${compressed.size.formatMag()}B",
            tag = tag
        )
        return compressed
    }

    actual fun decode(input: ByteArray): ByteArray {
        Napier.v("Inflating ${input.size.formatMag()}B input", tag = tag)

        //It's fine to push the whole data in at once, since Pako will split it into 65536 byte chunks internally
        return jsTry {
            val inflator = object : Pako.Inflate() {
                var numDecompressedBytes = 0
                override fun onData(chunk: Uint8Array) {
                    numDecompressedBytes += chunk.length
                    if (numDecompressedBytes > MAX_DECOMPRESSED_SIZE) {
                        Napier.v(
                            "Refusing to decompress and additional ${chunk.length.formatMag()}B from ${input.size.formatMag()}B input",
                            tag = tag
                        )
                        throw IllegalArgumentException("Decompression exceeded $MAX_DECOMPRESSED_SIZE bytes, is: $numDecompressedBytes! Input must be invalid.")
                    }
                    Napier.v(
                        "Decompressed ${numDecompressedBytes.formatMag()}B from ${input.size.formatMag()}B input",
                        tag = tag
                    )
                    super.onData(chunk)
                }
            }.also { it.push(input.toUint8Array()) }

            (inflator.result as Uint8Array).toByteArray()
        }.catch { throw it }
    }

}
