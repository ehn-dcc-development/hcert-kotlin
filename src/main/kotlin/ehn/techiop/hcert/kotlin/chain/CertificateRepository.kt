package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.trust.TrustedCertificate
import ehn.techiop.hcert.kotlin.trust.TrustedCertificateV1

interface CertificateRepository {

    fun loadTrustedCertificates(kid: ByteArray, verificationResult: VerificationResult): List<TrustedCertificate>

}