package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.trust.TrustListDecodeService
import ehn.techiop.hcert.kotlin.trust.TrustedCertificate


class TrustListCertificateRepository(input: ByteArray, certificateRepository: CertificateRepository) :
    CertificateRepository {

    private val list = TrustListDecodeService(certificateRepository).decode(input).certificates

    override fun loadTrustedCertificates(
        kid: ByteArray,
        verificationResult: VerificationResult
    ): List<TrustedCertificate> {
        val trustedCert = list.filter { it.kid contentEquals kid }
        if (trustedCert.isEmpty()) throw IllegalArgumentException("kid not known: $kid")
        return trustedCert
    }

}
