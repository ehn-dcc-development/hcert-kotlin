package ehn.techiop.hcert.kotlin.trust

import Cbor.Encoder
import Cbor.Wrappers
import Cbor.Wrappers.decodeFirst
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array

actual class CwtCreationAdapter actual constructor() {

    private val map = Wrappers.map()

    actual fun add(key: Int, value: Any) {
        when (value) {
            is ByteArray -> map.set(key, value.toUint8Array())
            is Long -> map.set(key, value.toInt())
            else -> map.set(key, value)
        }
    }

    actual fun addDgc(key: Int, innerKey: Int, input: ByteArray) {
        val innerMap = Wrappers.map()
        val value = decodeFirst(input)
        innerMap.set(innerKey, value)
        map.set(key, innerMap)
    }

    actual fun encode(): ByteArray {
        return Encoder.encode(map).toByteArray()
    }

}
