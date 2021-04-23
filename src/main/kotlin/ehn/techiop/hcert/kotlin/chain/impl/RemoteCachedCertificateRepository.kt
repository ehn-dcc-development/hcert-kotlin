package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.asBase64Url
import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.PublicKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class RemoteCachedCertificateRepository(private val baseUrl: String) : CertificateRepository {

    private val pkiUtils = PkiUtils()

    private val map = mutableMapOf<String, X509Certificate>()

    private fun byteArrayForKey(kid: ByteArray) = kid.asBase64Url()

    override fun loadPublicKey(kid: ByteArray, verificationResult: VerificationResult): PublicKey {
        val key = byteArrayForKey(kid)
        if (map.containsKey(key)) return map[key]!!.publicKey
        val request = Request.Builder().get().url("$baseUrl/$key").build()
        val response = OkHttpClient.Builder().build().newCall(request).execute()
        response.body?.let {
            val certificate =
                CertificateFactory.getInstance("X.509").generateCertificate(it.byteStream()) as X509Certificate
            verificationResult.certificateValidFrom = pkiUtils.getValidFrom(certificate)
            verificationResult.certificateValidUntil = pkiUtils.getValidUntil(certificate)
            // TODO verify that the certificate really has that kid
            map[key] = certificate
            return certificate.publicKey
        }
        throw IllegalArgumentException("Unable to get certificate for $kid ($key) at $baseUrl")
    }

    internal fun addCertificate(kid: ByteArray, certificate: X509Certificate) {
        val key = byteArrayForKey(kid)
        map[key] = certificate
    }

}


