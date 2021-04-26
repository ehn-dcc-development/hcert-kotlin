package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.asBase64Url
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import java.security.PublicKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class PrefilledCertificateRepository : CertificateRepository {

    private val map = mutableMapOf<String, X509Certificate>()

    constructor(vararg certificates: X509Certificate) {
        for (input in certificates) {
            map[byteArrayForKey(PkiUtils.calcKid(input))] = input
        }
    }

    constructor(vararg pemEncodedCertificates: String) {
        val factory = CertificateFactory.getInstance("X.509")
        for (input in pemEncodedCertificates) {
            val cert = factory.generateCertificate(input.byteInputStream()) as X509Certificate
            map[byteArrayForKey(PkiUtils.calcKid(cert))] = cert
        }
    }

    private fun byteArrayForKey(kid: ByteArray) = kid.asBase64Url()

    override fun loadPublicKey(kid: ByteArray, verificationResult: VerificationResult): PublicKey {
        val key = byteArrayForKey(kid)
        if (!map.containsKey(key))
            throw IllegalArgumentException("kid not known: $kid")
        val certificate = map[key]!!.also {
            verificationResult.certificateValidFrom = it.notBefore.toInstant()
            verificationResult.certificateValidUntil = it.notAfter.toInstant()
            verificationResult.certificateValidContent = PkiUtils.getValidContentTypes(it)
        }
        return certificate.publicKey
    }

}


