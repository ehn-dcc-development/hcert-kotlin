package ehn.techiop.hcert.kotlin.trust

expect class CwtCreationAdapter constructor() {

    fun add(key: Int, value: Any)

    fun addDgc(key: Int, innerKey: Int, input: ByteArray)

    fun encode(): ByteArray

}