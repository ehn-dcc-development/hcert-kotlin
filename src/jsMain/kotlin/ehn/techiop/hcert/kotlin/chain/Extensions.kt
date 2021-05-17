package ehn.techiop.hcert.kotlin.chain

import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

actual fun ByteArray.asBase64() = js("Buffer").from(this).toString("base64") as String

actual fun ByteArray.asBase64Url() = js("Buffer").from(this).toString("base64url") as String

actual fun ByteArray.toHexString() = joinToString("") { it.toString(16) }

actual fun String.fromBase64() = js("Buffer").from(this, "base64").unsafeCast<ByteArray>()

actual fun String.fromBase64Url() = js("Buffer").from(this, "base64url").unsafeCast<ByteArray>()

//See https://stackoverflow.com/a/66614516
actual fun String.fromHexString(): ByteArray {
    require(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

fun Map<Any, Any>.mapToJson() = js("{}").also { json ->
    forEach { json[it.key] = it.value }

fun ByteArray.toUint8Array(): Uint8Array {
    return Uint8Array(toTypedArray())
}

fun Uint8Array.toByteArray(): ByteArray {
    return ByteArray(this.length){ this[it] }
}
