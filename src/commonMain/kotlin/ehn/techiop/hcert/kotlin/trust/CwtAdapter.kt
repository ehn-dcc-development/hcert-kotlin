package ehn.techiop.hcert.kotlin.trust

expect class CwtAdapter constructor(input: ByteArray) {

    fun getMapEntryByteArray(value: Int): ByteArray?

    fun getMapEntryNumber(value: Int): Number?

}