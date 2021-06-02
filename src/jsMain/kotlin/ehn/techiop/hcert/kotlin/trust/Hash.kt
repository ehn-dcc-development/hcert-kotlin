package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array
import Hash as JsHash

actual class Hash actual constructor(private val input: ByteArray) {
    actual fun calc(): ByteArray {
        val hash = JsHash()
        hash.update(input.toUint8Array())
        return hash.digest().toByteArray()
    }
}