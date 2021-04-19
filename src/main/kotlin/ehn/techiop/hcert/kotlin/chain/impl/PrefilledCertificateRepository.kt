package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.asBase64Url
import java.security.cert.Certificate

class PrefilledCertificateRepository : CertificateRepository {

    private val map = mutableMapOf<String, Certificate>()

    private fun byteArrayForKey(kid: ByteArray) = kid.asBase64Url()

    override fun loadCertificate(kid: ByteArray): Certificate {
        val key = byteArrayForKey(kid)
        if (map.containsKey(key)) return map[key]!!
        throw IllegalArgumentException("kid not known: $kid")
    }

    fun addCertificate(kid: ByteArray, certificate: Certificate) {
        val key = byteArrayForKey(kid)
        map[key] = certificate
    }

}


