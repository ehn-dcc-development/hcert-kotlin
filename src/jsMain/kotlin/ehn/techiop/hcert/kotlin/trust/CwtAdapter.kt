package ehn.techiop.hcert.kotlin.trust

import Buffer
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array
import org.khronos.webgl.Uint8Array

actual class CwtAdapter actual constructor(private val input: ByteArray) {

    val map = Cbor.Decoder.decodeAllSync(Buffer.Companion.from(input.toUint8Array()))[0].asDynamic()

    actual fun getMapEntryByteArray(value: Int) = (map?.get(value) as Uint8Array?)?.toByteArray()

    actual fun getMapEntryNumber(value: Int) = map?.get(value) as Number?

}