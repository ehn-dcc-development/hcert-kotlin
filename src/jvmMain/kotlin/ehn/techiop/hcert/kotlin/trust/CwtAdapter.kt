package ehn.techiop.hcert.kotlin.trust

import com.upokecenter.cbor.CBORObject

actual class CwtAdapter actual constructor(private val input: ByteArray) {

    private val map = CBORObject.DecodeFromBytes(input)

    actual fun getByteArray(key: Int) = try {
        map[key]?.GetByteString()
    } catch (e: Throwable) {
        map[key]?.EncodeToBytes()
    }

    actual fun getString(key: Int) = map[key]?.AsString()

    actual fun getNumber(key: Int) = map[key]?.AsInt64() as Number?

    actual fun getDgcContent(outerKey: Int, innerKey: Int): ByteArray? {
        if (!map.ContainsKey(outerKey)) return null
        return try {
            map[outerKey][innerKey].EncodeToBytes()
        } catch (e: Throwable) {
            null
        }
    }

}