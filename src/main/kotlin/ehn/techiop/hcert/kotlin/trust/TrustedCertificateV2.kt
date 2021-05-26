package ehn.techiop.hcert.kotlin.trust

import COSE.OneKey
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.cbor.ByteString
import org.bouncycastle.cert.X509CertificateHolder
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.time.Instant


@Serializable
data class TrustedCertificateV2(
    @SerialName("i")
    @ByteString
    private val kid: ByteArray,

    @SerialName("c")
    @ByteString
    val certificate: ByteArray
) : TrustedCertificate {

    @Transient
    private val x509Certificate = CertificateFactory.getInstance("X.509").generateCertificate(certificate.inputStream())

    @Transient
    private val holder = X509CertificateHolder(x509Certificate.encoded)

    override fun getKid() = kid

    override fun getValidContentTypes(): List<ContentType> {
        return PkiUtils.getValidContentTypes(x509Certificate as X509Certificate)
    }

    override fun getValidFrom(): Instant {
        return holder.notBefore.toInstant()
    }

    override fun getValidUntil(): Instant {
        val holder = X509CertificateHolder(x509Certificate.encoded)
        return holder.notAfter.toInstant()
    }

    override fun buildOneKey(): OneKey {
        return OneKey(x509Certificate.publicKey, null)
    }

    companion object {
        fun fromCert(certificate: X509Certificate) = TrustedCertificateV2(
            kid = PkiUtils.calcKid(certificate),
            certificate = certificate.encoded,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrustedCertificateV2

        if (!kid.contentEquals(other.kid)) return false
        if (!certificate.contentEquals(other.certificate)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = kid.contentHashCode()
        result = 31 * result + certificate.contentHashCode()
        return result
    }

}