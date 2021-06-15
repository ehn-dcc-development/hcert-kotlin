package ehn.techiop.hcert.kotlin.trust

import Cbor.DecoderOptions
import ehn.techiop.hcert.kotlin.chain.catch
import ehn.techiop.hcert.kotlin.chain.fromHexString
import ehn.techiop.hcert.kotlin.chain.jsTry
import ehn.techiop.hcert.kotlin.chain.toBuffer
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toHexString
import org.khronos.webgl.Uint8Array

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class CwtAdapter actual constructor(private val input: ByteArray) {

    private val map = Cbor.Decoder.decodeFirstSync(input.toBuffer(), options = object : DecoderOptions {
        override var max_depth: Number? = 1
    }).asDynamic()

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

    actual fun getDgcContent(outerKey: Int, innerKey: Int): ByteArray? {
        return jsTry {
            val value = map?.get(outerKey)
            if (value == null || value == undefined) return null
            val innerValue = value.get(innerKey)
            if (innerValue == null || innerValue == undefined) return null
            // TODO We want the bytes, not the object ...
            //(innerValue as Uint8Array?)?.toByteArray()
            // we know, that the keys -260 and 1 are in there ... so'll we try to extract the content
            input.toHexString().uppercase().substringAfter("390103A101").fromHexString()
        }.catch {
            return null
        }
    }

}