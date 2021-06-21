package ehn.techiop.hcert.kotlin.chain

import java.util.*
import org.bouncycastle.util.encoders.Base64

actual fun ByteArray.asBase64() = Base64.toBase64String(this)

actual fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

actual fun String.fromBase64() = Base64.decode(this)

actual fun String.fromHexString() = chunked(2).map { it.toInt(16).toByte() }.toByteArray()