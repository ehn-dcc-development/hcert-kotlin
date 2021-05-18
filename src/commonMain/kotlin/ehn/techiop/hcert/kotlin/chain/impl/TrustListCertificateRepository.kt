package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.trust.TrustListDecodeService
import ehn.techiop.hcert.kotlin.trust.TrustedCertificate
import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.time.ExperimentalTime


@ExperimentalSerializationApi

class TrustListCertificateRepository(
    trustListSignature: ByteArray,
    trustListContent: ByteArray? = null,
    certificateRepository: CertificateRepository,
    clock: Clock = Clock.System,
) :
    CertificateRepository {

    private val list = TrustListDecodeService(certificateRepository, clock).decode(trustListSignature, trustListContent)

    override fun loadTrustedCertificates(
        kid: ByteArray,
        verificationResult: VerificationResult
    ): List<TrustedCertificate> {
        val certList = list.filter { it.kid contentEquals kid }
        if (certList.isEmpty()) throw IllegalArgumentException("kid")
        return certList
    }

}
