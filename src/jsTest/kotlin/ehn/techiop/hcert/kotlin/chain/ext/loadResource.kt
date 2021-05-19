package ehn.techiop.hcert.kotlin.chain.ext

import RHolder
import ehn.techiop.hcert.kotlin.chain.fromBase64

actual fun loadResource(filename: String): String? {
    val get = RHolder.get(filename)
    val fromBase64 = get?.fromBase64()
    val decodeToString = fromBase64?.decodeToString()
    return decodeToString
}