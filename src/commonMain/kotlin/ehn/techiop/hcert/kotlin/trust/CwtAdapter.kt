package ehn.techiop.hcert.kotlin.trust

expect class CwtAdapter constructor(input: ByteArray) {

    fun getByteArray(key: Int): ByteArray?

    fun getString(key: Int): String?

    fun getNumber(key: Int): Number?

    fun getMap(key: Int): CwtAdapter?

    fun encoded(): ByteArray

}