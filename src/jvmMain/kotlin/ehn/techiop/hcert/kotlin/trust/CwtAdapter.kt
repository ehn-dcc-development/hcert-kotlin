package ehn.techiop.hcert.kotlin.trust

import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.data.CborObject

actual object CwtHelper {
    actual fun fromCbor(input: ByteArray): CwtAdapter = JvmCwtAdapter(input)
}

class JvmCwtAdapter(input: ByteArray) : CwtAdapter {

    private val map = CBORObject.DecodeFromBytes(input)

    override fun getByteArray(key: Int) = try {
        map[key]?.GetByteString()
    } catch (e: Throwable) {
        map[key]?.EncodeToBytes()
    }

    override fun getString(key: Int) = map[key]?.AsString()

    override fun getNumber(key: Int) = map[key]?.AsInt64() as Number?

    override fun getMap(key: Int): CwtAdapter? {
        if (!map.ContainsKey(key)) return null
        return try {
            JvmCwtAdapter(map[key].GetByteString())
        } catch (e: Throwable) {
            JvmCwtAdapter(map[key].EncodeToBytes())
        }
    }

    override fun toCborObject(): CborObject = JvmCborObject(map)
    internal class JvmCborObject(private val cbor: CBORObject) : CborObject {
        override fun toJsonString() = cbor.ToJSONString()

        //if not present in object structure, this is technically a schema issue and we therefore do not handle it here
        override fun getVersionString() = try {
            cbor["ver"]?.AsString()
        } catch (t: Throwable) {
            null
        }
    }
}
