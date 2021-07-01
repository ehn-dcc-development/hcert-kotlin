package ehn.techiop.hcert.kotlin.trust

/**
 * Adapter to access SHA-256 hashing on all targets
 */
expect class Hash constructor(input: ByteArray) {
    fun calc(): ByteArray
}