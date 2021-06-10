package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter

interface TrustedCertificate {

    val kid: ByteArray

    fun toCertificateAdapter(): CertificateAdapter

}
