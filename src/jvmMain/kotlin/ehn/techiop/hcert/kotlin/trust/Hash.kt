package ehn.techiop.hcert.kotlin.trust

import java.security.MessageDigest

actual class Hash actual constructor(private val input: ByteArray) {
    actual fun calc() = MessageDigest.getInstance("SHA-256").digest(input)
}