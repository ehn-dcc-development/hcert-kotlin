package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.trust.SignedData
import ehn.techiop.hcert.kotlin.trust.TrustListDecodeService
import kotlinx.datetime.Clock


class TrustListCertificateRepository(
    trustList: SignedData,
    certificateRepository: CertificateRepository,
    clock: Clock = Clock.System,
) : CertificateRepository {

    private val list = TrustListDecodeService(certificateRepository, clock).decode(trustList).second.certificates

    override fun loadTrustedCertificates(
        kid: ByteArray,
        verificationResult: VerificationResult
    ): List<CertificateAdapter> {
        val certList = list.filter { it.kid contentEquals kid }
        if (certList.isEmpty())
            throw VerificationException(Error.KEY_NOT_IN_TRUST_LIST, "kid not found")

        return certList.map { it.toCertificateAdapter() }
    }

}
