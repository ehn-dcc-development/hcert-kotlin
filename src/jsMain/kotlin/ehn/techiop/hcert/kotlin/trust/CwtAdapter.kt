package ehn.techiop.hcert.kotlin.trust

import Buffer
import ehn.techiop.hcert.kotlin.chain.catch
import ehn.techiop.hcert.kotlin.chain.jsTry
import ehn.techiop.hcert.kotlin.chain.toBuffer
import ehn.techiop.hcert.kotlin.chain.toByteArray
import org.khronos.webgl.Uint8Array
import kotlin.js.Json

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class CwtAdapter actual constructor(private val input: ByteArray) {

    private val map = Cbor.Decoder.decodeAllSync(input.toBuffer())[0].asDynamic()

    actual fun getByteArray(key: Int): ByteArray? {
        return jsTry {
            (map.get(key) as Uint8Array?)?.toByteArray()
        }.catch {
            return null
        }
    }

    actual fun getString(key: Int): String? {
        return jsTry {
            map.get(key) as String?
        }.catch {
            return null
        }
    }

    actual fun getNumber(key: Int): Number? {
        return jsTry {
            map.get(key) as Number?
        }.catch {
            return null
        }
    }

    actual fun getMap(key: Int): CwtAdapter? {
        return jsTry {
            val value = map?.get(key)
            if (value == null || value == undefined) return null
            CwtAdapter(Cbor.Encoder.encode(value).toByteArray())
        }.catch {
            return null
        }
    }

    actual fun encoded() = input

}