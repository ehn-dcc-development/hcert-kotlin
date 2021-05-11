package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.trust.TrustListDecodeService
import ehn.techiop.hcert.kotlin.trust.TrustedCertificate
import java.time.Clock

class TrustListCertificateRepository(
    input: ByteArray,
    certificateRepository: CertificateRepository,
    clock: Clock = Clock.systemUTC(),
) :
    CertificateRepository {

    private val list = TrustListDecodeService(certificateRepository, clock).decode(input).certificates

    override fun loadTrustedCertificates(
        kid: ByteArray,
        verificationResult: VerificationResult
    ): List<TrustedCertificate> {
        val certList = list.filter { it.kid contentEquals kid }
        if (certList.isEmpty()) throw IllegalArgumentException("kid")
        return certList
    }

}
