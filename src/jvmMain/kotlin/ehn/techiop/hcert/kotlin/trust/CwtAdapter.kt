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

    actual fun getMap(key: Int): CwtAdapter? {
        if (!map.ContainsKey(key)) return null
        return try {
            CwtAdapter(map[key].GetByteString())
        } catch (e: Throwable) {
            CwtAdapter(map[key].EncodeToBytes())
        }
    }

    actual fun encoded() = input

}