package ehn.techiop.hcert.kotlin.chain.ext

import ehn.techiop.hcert.kotlin.chain.fromBase64

actual fun loadResource(filename: String): String? {
    val get = RHolder.get(filename)
    //println(get)
    val fromBase64 = get?.fromBase64()
    //println(fromBase64)
    val decodeToString = fromBase64?.decodeToString()
    //println(decodeToString)
    return decodeToString
}