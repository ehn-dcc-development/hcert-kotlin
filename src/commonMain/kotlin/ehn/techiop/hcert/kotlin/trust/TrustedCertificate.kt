package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.crypto.PublicKey
import kotlinx.datetime.Instant

interface TrustedCertificate {

    val kid: ByteArray

    val cosePublicKey: PublicKey<*>

    val validContentTypes: List<ContentType>

    val validFrom: Instant

    val validUntil: Instant

}
