package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.trust.TrustedCertificate

actual class PrefilledCertificateRepository : CertificateRepository {

    private val list = mutableListOf<CertificateAdapter>()

    actual constructor(vararg certificates: CertificateAdapter) {
        certificates.toList().forEach { list += it }
    }

    constructor(vararg pemEncodedCertificates: String) {
        pemEncodedCertificates.forEach { list += CertificateAdapter(it) }
    }

    actual constructor(pemEncoded: String) {
        list += CertificateAdapter(pemEncoded)
    }

    override fun loadTrustedCertificates(
        kid: ByteArray,
        verificationResult: VerificationResult
    ): List<TrustedCertificate> {
        val certList = list.filter { it.kid contentEquals kid }
        if (certList.isEmpty()) throw IllegalArgumentException("kid").also {
            verificationResult.error = Error.KEY_NOT_IN_TRUST_LIST
        }
        return certList.map { it.toTrustedCertificate() }
    }

}


