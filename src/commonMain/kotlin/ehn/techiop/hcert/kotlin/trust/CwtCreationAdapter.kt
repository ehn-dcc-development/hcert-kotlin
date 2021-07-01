package ehn.techiop.hcert.kotlin.trust

/**
 * Adapter to create and serialize CWT structures (CBOR maps) on all targets
 */
expect class CwtCreationAdapter constructor() {

    fun add(key: Int, value: Any)

    fun addDgc(key: Int, innerKey: Int, input: ByteArray)

    fun encode(): ByteArray

}