package ehn.techiop.hcert.kotlin.chain

import Buffer
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

actual fun ByteArray.asBase64() = Buffer.from(this.toUint8Array()).toString("base64")

actual fun ByteArray.toHexString() = joinToString("") { ('0' + (it.toUByte()).toString(16)).takeLast(2) }

actual fun String.fromBase64() = Buffer.from(this, "base64").toByteArray()

fun ArrayBuffer.toByteArray() = org.khronos.webgl.Int8Array(this).unsafeCast<ByteArray>()

fun ArrayBuffer.Companion.from(array: ByteArray) = Buffer.from(array).buffer

actual fun String.fromHexString() = Buffer.from(this, "hex").toByteArray()

fun ByteArray.toUint8Array(): Uint8Array {
    return Uint8Array(toTypedArray())
}

fun ByteArray.toBuffer(): Buffer = Buffer.from(toUint8Array())

fun Uint8Array.toByteArray(): ByteArray {
    return ByteArray(this.length) { this[it] }
}

fun Map<*, *>.mapToJson(): dynamic {
    val json = js("{}")
    this.forEach {
        val value = it.value
        if (value is Map<*, *>)
            json[it.key] = value.mapToJson()
        else
            json[it.key] = value
    }
    return json
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
