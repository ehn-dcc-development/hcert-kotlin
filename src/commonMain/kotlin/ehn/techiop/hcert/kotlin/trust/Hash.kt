package ehn.techiop.hcert.kotlin.trust

expect class Hash constructor(input: ByteArray) {
    fun calc(): ByteArray
}