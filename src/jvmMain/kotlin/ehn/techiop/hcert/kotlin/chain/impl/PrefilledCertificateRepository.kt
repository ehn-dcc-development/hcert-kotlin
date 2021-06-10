package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.fromBase64
import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.crypto.kid
import ehn.techiop.hcert.kotlin.trust.TrustedCertificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

actual class PrefilledCertificateRepository : CertificateRepository {

    private val list = mutableListOf<X509Certificate>()

    actual constructor(vararg certificates: CertificateAdapter) {
        certificates.forEach { list += it.certificate }
    }

    actual constructor(base64Encoded: String) {
        val factory = CertificateFactory.getInstance("X.509")
        list += factory.generateCertificate(base64Encoded.fromBase64().inputStream()) as X509Certificate
    }

    override fun loadTrustedCertificates(
        kid: ByteArray,
        verificationResult: VerificationResult
    ): List<TrustedCertificate> {
        val certList = list.filter { it.kid contentEquals kid }
        if (certList.isEmpty()) throw IllegalArgumentException("kid").also {
            verificationResult.error = Error.KEY_NOT_IN_TRUST_LIST
        }
        return certList.map { CertificateAdapter(it).toTrustedCertificate() }
    }

}


