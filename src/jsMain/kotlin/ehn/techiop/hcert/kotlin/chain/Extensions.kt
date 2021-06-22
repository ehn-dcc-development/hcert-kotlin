package ehn.techiop.hcert.kotlin.chain

import Buffer
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.Uint8ClampedArray
import org.khronos.webgl.get

actual fun ByteArray.asBase64() = Buffer.from(this.toUint8Array()).toString("base64")

actual fun ByteArray.toHexString() = joinToString("") { ('0' + (it.toUByte()).toString(16)).takeLast(2) }

actual fun String.fromBase64() = Buffer.from(this, "base64").toByteArray()

fun ArrayBuffer.toByteArray() = org.khronos.webgl.Int8Array(this).unsafeCast<ByteArray>()

fun ArrayBuffer.Companion.from(array: ByteArray) = Buffer.from(array).buffer

fun Buffer.toBase64UrlString() = this.toString("base64")
    .replace("+", "-")
    .replace("/", "_")
    .replace("=", "")

actual fun String.fromHexString() = Buffer.from(this, "hex").toByteArray()

fun ByteArray.toUint8Array(): Uint8Array {
    return Uint8Array(toTypedArray())
}

fun ByteArray.toUint8ClampedArray(): Uint8ClampedArray {
    return Uint8ClampedArray(toTypedArray())
}

fun ByteArray.toBuffer(): Buffer = Buffer.from(toUint8Array())

fun Uint8Array.toByteArray(): ByteArray {
    return ByteArray(this.length) { this[it] }
}

fun Buffer.Companion.from(arr: ByteArray): Buffer {
    return from(arr.toUint8Array())
}

internal object NullableTryCatch {

    internal sealed class Holder<T> {
        class Result<T>(internal val result: T) : Holder<T>()
        class Error<T>(internal val throwable: Throwable) : Holder<T>()
    }

    internal inline fun <reified T> jsTry(tryBlock: () -> T) = try {
        Holder.Result(tryBlock())
    } catch (e: dynamic) {
        val throwable = (if (e is Throwable) e else Throwable(JSON.stringify(e))) as Throwable
        Holder.Error(throwable)
    }

    internal inline fun <reified U, T : U> Holder<T>.catch(catchBlock: (t: Throwable) -> U) = when (this) {
        is Holder.Result<T> -> this.result
        is Holder.Error<T> -> catchBlock(this.throwable)
    }

    internal inline fun Holder<Unit>.catch(block: (t: Throwable) -> Unit) {
        if (this is Holder.Error<Unit>) block(this.throwable)
    }

}


