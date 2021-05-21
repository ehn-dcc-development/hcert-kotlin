package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.crypto.PubKey
import kotlinx.datetime.Instant

interface TrustedCertificate {

    val kid: ByteArray

    val cosePublicKey: PubKey<*>

    val validContentTypes: List<ContentType>

    val validFrom: Instant

    val validUntil: Instant

}
