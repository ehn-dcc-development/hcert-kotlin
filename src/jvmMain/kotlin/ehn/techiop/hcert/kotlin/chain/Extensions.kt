package ehn.techiop.hcert.kotlin.chain

import java.util.*

actual fun ByteArray.asBase64() = Base64.getEncoder().encodeToString(this)

actual fun ByteArray.asBase64Url() = Base64.getUrlEncoder().encodeToString(this)

actual fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

actual fun String.fromBase64() = Base64.getDecoder().decode(this)

actual fun String.fromBase64Url() = Base64.getUrlDecoder().decode(this)

actual fun String.fromHexString() = chunked(2).map { it.toInt(16).toByte() }.toByteArray()


