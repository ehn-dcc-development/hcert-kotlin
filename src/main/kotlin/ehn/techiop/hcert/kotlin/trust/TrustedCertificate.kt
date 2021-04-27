package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import ehn.techiop.hcert.kotlin.data.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.ByteString
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import java.security.cert.X509Certificate
import java.security.interfaces.ECPublicKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant


@Serializable
data class TrustedCertificate(
    @SerialName("f")
    @Serializable(with = InstantSerializer::class)
    val validFrom: Instant,

    @SerialName("u")
    @Serializable(with = InstantSerializer::class)
    val validUntil: Instant,

    @SerialName("i")
    @ByteString
    val kid: ByteArray,

    @SerialName("k")
    val keyType: KeyType,

    /**
     * PKCS#1 encoding, i.e. without algorithm identifiers around it
     */
    @SerialName("p")
    @ByteString
    val publicKey: ByteArray,

    @SerialName("t")
    val validContentTypes: List<ContentType>,
) {
    companion object {
        fun fromCert(certificate: X509Certificate) = TrustedCertificate(
            validFrom = certificate.notBefore.toInstant(),
            validUntil = certificate.notAfter.toInstant(),
            kid = PkiUtils.calcKid(certificate),
            keyType = when (certificate.publicKey) {
                is RSAPublicKey -> KeyType.RSA
                is ECPublicKey -> KeyType.EC
                else -> throw IllegalArgumentException("keyType")
            },
            publicKey = SubjectPublicKeyInfo.getInstance(certificate.publicKey.encoded).publicKeyData.bytes,
            validContentTypes = PkiUtils.getValidContentTypes(certificate)
        )

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrustedCertificate

        if (validFrom != other.validFrom) return false
        if (validUntil != other.validUntil) return false
        if (!kid.contentEquals(other.kid)) return false
        if (keyType != other.keyType) return false
        if (!publicKey.contentEquals(other.publicKey)) return false
        if (validContentTypes != other.validContentTypes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = validFrom.hashCode()
        result = 31 * result + validUntil.hashCode()
        result = 31 * result + kid.contentHashCode()
        result = 31 * result + keyType.hashCode()
        result = 31 * result + publicKey.contentHashCode()
        result = 31 * result + validContentTypes.hashCode()
        return result
    }
}