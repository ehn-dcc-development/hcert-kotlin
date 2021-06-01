package ehn.techiop.hcert.kotlin.chain.impl

import java.util.zip.Deflater
import java.util.zip.DeflaterInputStream
import java.util.zip.InflaterInputStream

actual open class CompressorAdapter {

    actual fun encode(input: ByteArray, level: Int) =
        DeflaterInputStream(input.inputStream(), Deflater(level)).readBytes()

    actual fun decode(input: ByteArray) =
        InflaterInputStream(input.inputStream()).readBytes()

}
