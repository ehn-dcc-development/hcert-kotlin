package ehn.techiop.hcert.kotlin.chain

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

    // TODO SPKI encoden!
    @SerialName("p")
    @ByteString
    val publicKey: ByteArray,

    @SerialName("t")
    val certType: List<CertType>,
) {
    companion object {
        fun fromCert(kid: ByteArray, certificate: X509Certificate) = TrustedCertificate(
            validFrom = certificate.notBefore.toInstant(),
            validUntil = certificate.notAfter.toInstant(),
            kid = kid,
            keyType = when (certificate.publicKey) {
                is RSAPublicKey -> KeyType.RSA
                is ECPublicKey -> KeyType.EC
                else -> throw IllegalArgumentException("Unknown key type")
            },
            publicKey = certificate.publicKey.encoded,
            // TODO read from OID, per default for all types accepted
            certType = listOf(CertType.VACCINATION, CertType.TEST, CertType.RECOVERY)
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
    @SerialName("v")
    VACCINATION,

    @SerialName("t")
    TEST,

    @SerialName("r")
    RECOVERY;
}