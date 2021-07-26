package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys

/**
 * Holds a [content] and detached [signature] in a certain format:
 * [signature] is an encoded COSE structure with protected header keys
 * - [CoseHeaderKeys.TRUSTLIST_VERSION]
 * - [CoseHeaderKeys.KID]: KID of the signer certificate
 * The content of that COSE structure is a CWT map containing
 * - [CwtHeaderKeys.NOT_BEFORE]: seconds since UNIX epoch
 * - [CwtHeaderKeys.EXPIRATION]: seconds since UNIX epoch
 * - [CwtHeaderKeys.SUBJECT]: the SHA-256 hash of the content file
 */
data class SignedData(
    val content: ByteArray,
    val signature: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SignedData

        if (!content.contentEquals(other.content)) return false
        if (!signature.contentEquals(other.signature)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content.contentHashCode()
        result = 31 * result + signature.contentHashCode()
        return result
    }
}
