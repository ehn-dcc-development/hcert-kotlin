package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.crypto.JsCertificate
import ehn.techiop.hcert.kotlin.trust.TrustedCertificate

actual class PrefilledCertificateRepository : CertificateRepository {

    private val list = mutableListOf<JsCertificate>()

    constructor(vararg certificates: JsCertificate) {
        certificates.forEach { list += it }
    }

    constructor(vararg pemEncodedCertificates: String) {
        pemEncodedCertificates.forEach {
            list += JsCertificate(it)
        }
    }

    actual constructor(input: ByteArray) {
        list += JsCertificate(input)
    }

    actual constructor(base64Encoded: String) {
        list += JsCertificate(base64Encoded)
    }

    override fun loadTrustedCertificates(
        kid: ByteArray,
        verificationResult: VerificationResult
    ): List<TrustedCertificate> {
        val certList = list.filter { it.kid contentEquals kid }
        if (certList.isEmpty()) throw IllegalArgumentException("kid")
        // TODO return all trusted certificates
        return listOf(list[0].toTrustedCertificate())
    }

}


