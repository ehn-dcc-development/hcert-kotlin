package ehn.techiop.hcert.kotlin.trust

import com.upokecenter.cbor.CBORObject

actual class CwtAdapter actual constructor(private val input: ByteArray) {

    val map = CBORObject.DecodeFromBytes(input)

    actual fun getMapEntryByteArray(value: Int) = map[value]?.GetByteString()

    actual fun getMapEntryNumber(value: Int) = map[value]?.AsInt64() as Number?
}