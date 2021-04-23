package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.asBase64Url
import java.security.PublicKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class PrefilledCertificateRepository : CertificateRepository {

    private val pkiUtils = PkiUtils()
    private val map = mutableMapOf<String, X509Certificate>()

    constructor(vararg certificates: X509Certificate) {
        for (input in certificates) {
            map[byteArrayForKey(pkiUtils.calcKid(input))] = input
        }
    }

    constructor(vararg pemEncodedCertificates: String) {
        val factory = CertificateFactory.getInstance("X.509")
        for (input in pemEncodedCertificates) {
            val cert = factory.generateCertificate(input.byteInputStream()) as X509Certificate
            map[byteArrayForKey(pkiUtils.calcKid(cert))] = cert
        }
    }

    private fun byteArrayForKey(kid: ByteArray) = kid.asBase64Url()

    override fun loadPublicKey(kid: ByteArray, verificationResult: VerificationResult): PublicKey {
        val key = byteArrayForKey(kid)
        if (!map.containsKey(key))
            throw IllegalArgumentException("kid not known: $kid")
        val x509Certificate = map[key]!!
        verificationResult.certificateValidFrom = pkiUtils.getValidFrom(x509Certificate)
        verificationResult.certificateValidUntil = pkiUtils.getValidUntil(x509Certificate)
        return x509Certificate.publicKey
    }

}


