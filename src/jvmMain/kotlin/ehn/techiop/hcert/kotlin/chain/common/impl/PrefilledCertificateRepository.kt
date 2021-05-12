package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils.toTrustedCertificate
import ehn.techiop.hcert.kotlin.trust.TrustedCertificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class PrefilledCertificateRepository : CertificateRepository {

    private val list = mutableListOf<X509Certificate>()

    constructor(vararg certificates: X509Certificate) {
        certificates.forEach { list += it }
    }

    constructor(vararg pemEncodedCertificates: String) {
        val factory = CertificateFactory.getInstance("X.509")
        pemEncodedCertificates.forEach {
            list += factory.generateCertificate(it.byteInputStream()) as X509Certificate
        }
    }

    override fun loadTrustedCertificates(
        kid: ByteArray,
        verificationResult: VerificationResult
    ): List<TrustedCertificate> {
        val certList = list.filter { PkiUtils.calcKid(it) contentEquals kid }
        if (certList.isEmpty()) throw IllegalArgumentException("kid")
        return certList.map { it.toTrustedCertificate() }
    }

}


