package ehn.techiop.hcert.kotlin.chain


expect fun ByteArray.asBase64(): String

expect fun ByteArray.asBase64Url(): String

expect fun ByteArray.toHexString(): String

expect fun String.fromBase64(): ByteArray

expect fun String.fromBase64Url(): ByteArray

expect fun String.fromHexString(): ByteArray
