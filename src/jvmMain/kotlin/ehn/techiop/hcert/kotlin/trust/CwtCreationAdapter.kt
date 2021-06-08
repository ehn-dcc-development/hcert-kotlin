package ehn.techiop.hcert.kotlin.trust

import com.upokecenter.cbor.CBORObject

actual class CwtCreationAdapter actual constructor() {

    private val map = mutableMapOf<CBORObject, CBORObject>()

    actual fun add(key: Int, value: Any) {
        map[CBORObject.FromObject(key)] = CBORObject.FromObject(value)
    }

    actual fun addDgc(key: Int, innerKey: Int, input: ByteArray) {
        map[CBORObject.FromObject(key)] = CBORObject.NewMap().also {
            try {
                it[CBORObject.FromObject(innerKey)] = CBORObject.DecodeFromBytes(input)
            } catch (e: Exception) {
                it[CBORObject.FromObject(innerKey)] = CBORObject.FromObject(input)
            }
        }
    }

    actual fun encode(): ByteArray {
        val result = CBORObject.NewMap()
        map.forEach { result[it.key] = it.value }
        return result.EncodeToBytes()
    }


}