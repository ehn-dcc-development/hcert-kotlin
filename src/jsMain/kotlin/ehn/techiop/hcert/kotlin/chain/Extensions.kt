package ehn.techiop.hcert.kotlin.chain

import Buffer
import base64url
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.fetch.Request

actual fun ByteArray.asBase64() = Buffer.from(this.toUint8Array()).toString("base64")

actual fun ByteArray.asBase64Url() = base64url.toBase64(Buffer.from(this.toUint8Array()))

actual fun ByteArray.toHexString() = joinToString("") { ('0' + (it.toUByte()).toString(16)).takeLast(2) }

actual fun String.fromBase64() = Buffer.from(this, "base64").toByteArray()

actual fun String.fromBase64Url() = base64url.toBuffer(this).toByteArray()

//See https://stackoverflow.com/a/66614516
actual fun String.fromHexString(): ByteArray {
    require(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

fun ByteArray.toUint8Array(): Uint8Array {
    return Uint8Array(toTypedArray())
}

fun Uint8Array.toByteArray(): ByteArray {
    return ByteArray(this.length) { this[it] }
}

fun Map<Any, Any>.mapToJson() = js("{}").also { json ->
    forEach { json[it.key] = it.value }
}

fun toMap(container: dynamic): HashMap<String, Any> {
    val m = HashMap<String, Any>().asDynamic()
    m.map = container
    val keys = js("Object.keys")
    m.`$size` = keys(container).length
    return m
}

fun Buffer.Companion.from(arr: ByteArray): Buffer {
    return from(arr.toUint8Array())
}

internal class H<T>(internal val result: T?, internal val err: Throwable?)

internal inline fun <reified T> jsTry(block: () -> T): H<T> {
    return try {
        H(block(), null)
    } catch (e: dynamic) {
        H(null, (if (e is Throwable) e else Throwable(JSON.stringify(e))) as? Throwable)
    }
}

internal inline fun <reified T> H<T>.catch(block: (t: Throwable) -> T): T {
    return if (this.err == null) result!! else block(this.err)
}

internal inline fun H<Unit>.catch(block: (t: Throwable) -> Unit) {
    if (this.err != null) block(this.err)
}