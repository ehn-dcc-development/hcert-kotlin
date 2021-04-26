package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import ehn.techiop.hcert.kotlin.data.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.ByteString
import java.security.cert.X509Certificate
import java.security.interfaces.ECPublicKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant

@Serializable
data class TrustList(
    @SerialName("f")
    @Serializable(with = InstantSerializer::class)
    val validFrom: Instant,

    @SerialName("u")
    @Serializable(with = InstantSerializer::class)
    val validUntil: Instant,

    @SerialName("c")
    val certificates: List<TrustedCertificate>
)

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

    @SerialName("p")
    @ByteString
    val publicKey: ByteArray,

    @SerialName("t")
    val certType: List<CertType>,
) {
    companion object {
        fun fromCert(certificate: X509Certificate) = TrustedCertificate(
            validFrom = certificate.notBefore.toInstant(),
            validUntil = certificate.notAfter.toInstant(),
            kid = PkiUtils.calcKid(certificate),
            keyType = when (certificate.publicKey) {
                is RSAPublicKey -> KeyType.RSA
                is ECPublicKey -> KeyType.EC
                else -> throw IllegalArgumentException("Unknown key type")
            },
            publicKey = certificate.publicKey.encoded,
            certType = PkiUtils.getValidContentTypes(certificate)
        )

    }
}

@Serializable
enum class KeyType {
    @SerialName("r")
    RSA,

    @SerialName("e")
    EC
}

@Serializable
enum class CertType {
    @SerialName("t")
    TEST,

    @SerialName("v")
    VACCINATION,

    @SerialName("r")
    RECOVERY;
}
