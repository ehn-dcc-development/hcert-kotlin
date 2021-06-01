package ehn.techiop.hcert.kotlin.chain.impl

expect class CompressorAdapter() {

    fun encode(input: ByteArray, level: Int): ByteArray

    fun decode(input: ByteArray): ByteArray

}