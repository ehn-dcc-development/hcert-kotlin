package ehn.techiop.hcert.kotlin.trust

import Buffer
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array
import org.khronos.webgl.Uint8Array

actual class CwtAdapter actual constructor(private val input: ByteArray) {

    val map = Cbor.Decoder.decodeAllSync(Buffer.Companion.from(input.toUint8Array()))[0].asDynamic()

    actual fun getByteArray(key: Int) = (map?.get(key) as Uint8Array?)?.toByteArray()

    actual fun getString(key: Int) = map?.get(key) as String?

    actual fun getNumber(key: Int) = map?.get(key) as Number?

    actual fun getMap(key: Int): CwtAdapter? {
        val value = map?.get(key)
        if (value == null || value == undefined) return null
        return CwtAdapter(Cbor.Encoder.encode(value).toByteArray())
    }

    actual fun encoded() = input


}