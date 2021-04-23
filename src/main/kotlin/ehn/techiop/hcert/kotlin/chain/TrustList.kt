package ehn.techiop.hcert.kotlin.chain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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

    @SerialName("k")
    val kid: ByteArray,

    @SerialName("t")
    val keyType: KeyType,

    @SerialName("p")
    val publicKey: ByteArray,
) {
    companion object {
        fun fromCert(kid: ByteArray, certificate: X509Certificate) = TrustedCertificate(
            kid = kid,
            validFrom = certificate.notBefore.toInstant(),
            validUntil = certificate.notAfter.toInstant(),
            keyType = when (certificate.publicKey) {
                is RSAPublicKey -> KeyType.RSA
                is ECPublicKey -> KeyType.EC
                else -> throw IllegalArgumentException("Unknown key type")
            },
            publicKey = certificate.publicKey.encoded
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
