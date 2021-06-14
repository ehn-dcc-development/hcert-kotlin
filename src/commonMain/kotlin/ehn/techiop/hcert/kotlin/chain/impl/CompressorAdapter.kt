package ehn.techiop.hcert.kotlin.chain.impl

//Work around https://youtrack.jetbrains.com/issue/KT-21186
object CompressionConstants {
    const val MAX_DECOMPRESSED_SIZE = 5 * 1024 * 1024}

expect class CompressorAdapter() {


    fun encode(input: ByteArray, level: Int): ByteArray

    fun decode(input: ByteArray): ByteArray

}