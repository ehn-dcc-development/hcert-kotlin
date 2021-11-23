package ehn.techiop.hcert.kotlin.trust

import COSE.Message
import COSE.Sign1Message
import COSE.signature
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.asBase64
import ehn.techiop.hcert.kotlin.chain.toHexString
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CwtAlgorithm
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys
import ehn.techiop.hcert.kotlin.data.CborObject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

actual object CwtHelper {
    actual fun fromCbor(input: ByteArray): CwtAdapter = JvmCwtAdapter(input)
}

private val jsonHelper = Json { prettyPrint = true }

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

    override fun toString(): String {
        val msg = Message.DecodeFromBytes(map.EncodeToBytes())
        val payload = msg.GetContent()


        val unprotecedHeader = extractMap(msg.unprotectedAttributes)
        val protecedHeader = extractMap(msg.protectedAttributes)


        val json = JsonObject(
            mapOf(
                "protectedHeader" to Json.encodeToJsonElement(protecedHeader),
                "payload" to JsonPrimitive(payload.toHexString()),
                "type" to JsonPrimitive(msg::class.java.simpleName),
                "unprotectedHeader" to Json.encodeToJsonElement(unprotecedHeader),
            ).let { map ->
                if (msg is Sign1Message) map.toMutableMap().also {
                    it["signature"] =
                        JsonPrimitive(msg.signature.asBase64())
                } else map
            }
        )

        return jsonHelper.encodeToString(json)
    }

    private fun extractMap(attributes: CBORObject?): MutableMap<String, String> {
        val unprotecedHeader = mutableMapOf<String, String>()
        attributes?.let {
            it.keys.map { key ->
                val parserHeaderKey =
                    CoseHeaderKeys.fromIntVal(key.AsInt32())?.stringVal ?: key.AsInt32()
                        .toString()
                val value = if (parserHeaderKey == CoseHeaderKeys.ALGORITHM.stringVal) {

                    CwtAlgorithm.fromIntVal(attributes[key].AsInt32())?.stringVal ?: attributes[key].EncodeToBytes()
                        .toHexString()
                } else attributes[key].EncodeToBytes().toHexString()
                unprotecedHeader[parserHeaderKey] = value
            }
        }
        attributes?.let {
            unprotecedHeader["rawHeader"] = it.EncodeToBytes().toHexString()
        }
        return unprotecedHeader
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
