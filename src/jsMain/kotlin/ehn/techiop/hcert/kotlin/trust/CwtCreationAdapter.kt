package ehn.techiop.hcert.kotlin.trust

import Cbor.DecoderOptions
import Cbor.Encoder
import Cbor.Map
import ehn.techiop.hcert.kotlin.chain.toBuffer
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array

actual class CwtCreationAdapter actual constructor() {

    private val map = Map(js("([])"))

    actual fun add(key: Int, value: Any) {
        if (value is ByteArray) {
            map.set(key, value.toUint8Array())
        } else if (value is Long) {
            map.set(key, value.toInt())
        } else {
            map.set(key, value)
        }
    }

    actual fun addDgc(key: Int, innerKey: Int, input: ByteArray) {
        val innerMap = Map(js("([])"))
        val value = Cbor.Decoder.decodeFirstSync(input = input.toBuffer(), options = object : DecoderOptions {})
        innerMap.set(innerKey, value)
        map.set(key, innerMap)
    }

    actual fun encode(): ByteArray {
        return Encoder.encode(map).toByteArray()
    }

}
