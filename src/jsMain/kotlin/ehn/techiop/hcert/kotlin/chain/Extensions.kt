package ehn.techiop.hcert.kotlin.chain

actual fun ByteArray.asBase64(): String {
    TODO("Not yet implemented")
}

actual fun ByteArray.asBase64Url(): String {
    TODO("Not yet implemented")
}

actual fun ByteArray.toHexString(): String {
    TODO("Not yet implemented")
}

actual fun String.fromBase64(): ByteArray {
    TODO("Not yet implemented")
}

actual fun String.fromBase64Url(): ByteArray {
    TODO("Not yet implemented")
}

//See https://stackoverflow.com/a/66614516
actual fun String.fromHexString(): ByteArray {
    require(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}