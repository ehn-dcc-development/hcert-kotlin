package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.TrustedCertificate
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class PrefilledCertificateRepository : CertificateRepository {

    private val list = mutableListOf<X509Certificate>()

    constructor(vararg certificates: X509Certificate) {
        certificates.forEach { list += it }
    }

    constructor(vararg pemEncodedCertificates: String) {
        val factory = CertificateFactory.getInstance("X.509")
        for (input in pemEncodedCertificates) {
            val cert = factory.generateCertificate(input.byteInputStream()) as X509Certificate
            list += cert
        }
    }

    override fun loadTrustedCertificates(kid: ByteArray, verificationResult: VerificationResult): List<TrustedCertificate> {
        val certList = list.filter { PkiUtils.calcKid(it) contentEquals kid }
        if (certList.isEmpty()) throw IllegalArgumentException("kid not known: $kid")
        return certList.map { TrustedCertificate.fromCert(it) }
    }

}


