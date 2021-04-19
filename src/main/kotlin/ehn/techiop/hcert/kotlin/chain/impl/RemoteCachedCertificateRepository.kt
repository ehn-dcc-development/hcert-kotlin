package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.asBase64Url
import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.cert.Certificate
import java.security.cert.CertificateFactory

class RemoteCachedCertificateRepository(private val baseUrl: String) : CertificateRepository {

    private val map = mutableMapOf<String, Certificate>()

    private fun byteArrayForKey(kid: ByteArray) = kid.asBase64Url()

    override fun loadCertificate(kid: ByteArray): Certificate {
        val key = byteArrayForKey(kid)
        if (map.containsKey(key)) return map[key]!!
        val request = Request.Builder().get().url("$baseUrl/$key").build()
        val response = OkHttpClient.Builder().build().newCall(request).execute()
        response.body?.let {
            val certificate = CertificateFactory.getInstance("X.509").generateCertificate(it.byteStream())
            // TODO verify that the certificate really has that kid
            map[key] = certificate
            return certificate
        }
        throw IllegalArgumentException("Unable to get certificate for $kid ($key) at $baseUrl")
    }

    internal fun addCertificate(kid: ByteArray, certificate: Certificate) {
        val key = byteArrayForKey(kid)
        map[key] = certificate
    }

}


