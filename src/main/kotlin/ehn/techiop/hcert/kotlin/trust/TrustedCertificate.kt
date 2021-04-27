package ehn.techiop.hcert.kotlin.trust

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
            publicKey = certificate.publicKey.encoded,
            validContentTypes = PkiUtils.getValidContentTypes(certificate)
        )

    }
}