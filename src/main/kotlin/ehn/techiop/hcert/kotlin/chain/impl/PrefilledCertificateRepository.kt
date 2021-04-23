package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.asBase64Url
import java.security.PublicKey
import java.security.cert.X509Certificate

class PrefilledCertificateRepository : CertificateRepository {

    private val pkiUtils = PkiUtils()

    private val map = mutableMapOf<String, X509Certificate>()

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

    fun addCertificate(kid: ByteArray, certificate: X509Certificate) {
        val key = byteArrayForKey(kid)
        map[key] = certificate
    }

}


