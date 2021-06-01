package ehn.techiop.hcert.kotlin.trust

import Cbor.DecodeOptions
import ehn.techiop.hcert.kotlin.chain.mapToJson
import ehn.techiop.hcert.kotlin.chain.toBuffer
import ehn.techiop.hcert.kotlin.chain.toByteArray

actual class CwtCreationAdapter actual constructor() {

    val map = mutableMapOf<Any, Any>()

    actual fun add(key: Int, value: Any) {
        map[key] = value
    }

    actual fun addDgc(key: Int, innerKey: Int, input: ByteArray) {
        // TODO Verify if this is correct, see JVM implementation
        map[key] = mapOf(
            innerKey to Cbor.Decoder.decodeFirstSync(input = input.toBuffer(), options = object : DecodeOptions {})
        )
    }

    actual fun encode(): ByteArray {
        val export = map.mapToJson()
        val buffer = Cbor.Encoder.encode(export)
        return buffer.toByteArray()
    }


}