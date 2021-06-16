package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.catch
import ehn.techiop.hcert.kotlin.chain.jsTry
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

    override fun getByteArray(key: Int): ByteArray? {
        return jsTry {
            (map.get(key) as Uint8Array?)?.toByteArray()
        }.catch {
            return null
        }
    }

    override fun getString(key: Int): String? {
        return jsTry {
            map.get(key) as String?
        }.catch {
            return null
        }
    }

    override fun getNumber(key: Int): Number? {
        return jsTry {
            map.get(key) as Number?
        }.catch {
            return null
        }
    }

    override fun getMap(key: Int): CwtAdapter? {
        return jsTry {
            val value = map?.get(key)
            if (value == null || value == undefined) return null
            JsCwtAdapter(value)
        }.catch {
            return null
        }
    }

    //This seems gruesome, but works on JS since the Interface does not declare any members
    override fun toCborObject(): CborObject = JsCborObject(map)
    class JsCborObject(internal val internalRepresentation: dynamic) : CborObject
}