package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.NullableTryCatch.catch
import ehn.techiop.hcert.kotlin.chain.NullableTryCatch.jsTry
import ehn.techiop.hcert.kotlin.chain.toBuffer
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.data.CborObject
import org.khronos.webgl.Uint8Array

actual object CwtHelper {
    actual fun fromCbor(input: ByteArray): CwtAdapter =
        JsCwtAdapter(Cbor.Decoder.decodeAllSync(input.toBuffer())[0].asDynamic())
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
class JsCwtAdapter(private val map: dynamic) : CwtAdapter {

    override fun getByteArray(key: Int): ByteArray? =
        jsTry { (map.get(key) as Uint8Array?)?.toByteArray() }.catch { null }

    override fun getString(key: Int): String? =
        jsTry { map.get(key) as String? }.catch { null }

    override fun getNumber(key: Int): Number? =
        jsTry { map.get(key) as Number? }.catch { null }

    override fun getMap(key: Int): CwtAdapter? =
        jsTry {
            val value = map?.get(key)
            if (value == null || value == undefined) return null
            JsCwtAdapter(value)
        }.catch {
            null
        }


    //This seems gruesome, but works on JS since the Interface does not declare any members
    override fun toCborObject(): CborObject = JsCborObject(map)
    class JsCborObject(internal val internalRepresentation: dynamic) : CborObject {
        override fun toJsonString() = JSON.stringify(internalRepresentation)

        //if not present in object structure, this is technically a schema issue and we therefore do not handle it here
        override fun getVersionString(): String? = jsTry { internalRepresentation["ver"] as String? }.catch { null }
    }
}