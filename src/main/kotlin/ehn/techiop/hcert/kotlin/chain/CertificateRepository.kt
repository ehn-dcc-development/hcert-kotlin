package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.trust.TrustedCertificate

interface CertificateRepository {

    fun loadTrustedCertificates(kid: ByteArray, verificationResult: VerificationResult): List<TrustedCertificate>

}