package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter

/**
 * Interface for a "trusted" certificate to allow more versions
 * of a [TrustedCertificateV2] and [TrustListV2]
 */
interface TrustedCertificate {

    val kid: ByteArray

    fun toCertificateAdapter(): CertificateAdapter

}
