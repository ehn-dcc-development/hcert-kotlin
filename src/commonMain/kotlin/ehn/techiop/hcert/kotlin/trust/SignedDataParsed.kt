package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import kotlinx.datetime.Instant

data class SignedDataParsed(
    val validFrom: Instant,
    val validUntil: Instant,
    val content: ByteArray,
    // TODO how to get protected headers to any type
    val headers: Map<CoseHeaderKeys, Int?>
)