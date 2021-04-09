package ehn.techiop.hcert.kotlin.chain

import java.util.*

fun ByteArray.asBase64() = Base64.getEncoder().encodeToString(this)

fun ByteArray.asBase64Url() = Base64.getUrlEncoder().encodeToString(this)

fun String.fromBase64() = Base64.getDecoder().decode(this)

