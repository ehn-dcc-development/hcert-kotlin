package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.data.CborObject

/**
 * Adapter to deserialize CWT structures (CBOR maps) on all targets
 */
interface CwtAdapter {

    fun getByteArray(key: Int): ByteArray?

    fun getString(key: Int): String?

    fun getNumber(key: Int): Number?

    fun getMap(key: Int): CwtAdapter?

    fun toCborObject(): CborObject
}

expect object CwtHelper {
    fun fromCbor(input: ByteArray): CwtAdapter
}
