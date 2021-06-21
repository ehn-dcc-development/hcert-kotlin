package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter

class PrefilledCertificateRepository : CertificateRepository {

    private val list = mutableListOf<CertificateAdapter>()

    constructor(vararg certificates: CertificateAdapter) {
        certificates.forEach { list += it }
    }

    constructor(vararg pemEncodedCertificates: String) {
        pemEncodedCertificates.forEach { list += CertificateAdapter(it) }
    }

    constructor(pemEncoded: String) {
        list += CertificateAdapter(pemEncoded)
    }

    override fun loadTrustedCertificates(
        kid: ByteArray,
        verificationResult: VerificationResult
    ): List<CertificateAdapter> {
        val certList = list.filter { it.kid contentEquals kid }
        if (certList.isEmpty())
            throw VerificationException(Error.KEY_NOT_IN_TRUST_LIST, "kid not found")

        return certList
    }

}


