package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import kotlinx.datetime.Instant

/**
 * Information parsed from [SignedData]
 */
data class SignedDataParsed(
    val validFrom: Instant,
    val validUntil: Instant,
    val content: ByteArray,
    val headers: Map<CoseHeaderKeys, Int?>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SignedDataParsed

        if (validFrom != other.validFrom) return false
        if (validUntil != other.validUntil) return false
        if (!content.contentEquals(other.content)) return false
        if (headers != other.headers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = validFrom.hashCode()
        result = 31 * result + validUntil.hashCode()
        result = 31 * result + content.contentHashCode()
        result = 31 * result + headers.hashCode()
        return result
    }
}