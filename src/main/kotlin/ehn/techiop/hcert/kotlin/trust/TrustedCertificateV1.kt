package ehn.techiop.hcert.kotlin.trust

import COSE.OneKey
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import ehn.techiop.hcert.kotlin.data.InstantLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.ByteString
import org.bouncycastle.asn1.sec.SECNamedCurves
import org.bouncycastle.asn1.sec.SECObjectIdentifiers
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.jce.ECPointUtil
import org.bouncycastle.jce.spec.ECNamedCurveSpec
import java.security.KeyFactory
import java.security.cert.X509Certificate
import java.security.interfaces.ECPublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.ECPublicKeySpec
import java.security.spec.RSAPublicKeySpec
import java.time.Instant


@Serializable
data class TrustedCertificateV1(
    @SerialName("f")
    @Serializable(with = InstantLongSerializer::class)
    private val validFrom: Instant,

    @SerialName("u")
    @Serializable(with = InstantLongSerializer::class)
    private val validUntil: Instant,

    @SerialName("i")
    @ByteString
    private val kid: ByteArray,

    @SerialName("k")
    val keyType: KeyType,

    /**
     * Public key in PKCS#1 encoding, i.e. without algorithm identifiers around it.
     * For EC: "04 || X || Y".
     * For RSA: "ASN1-SEQ { MODULUS, EXPONENT }"
     */
    @SerialName("p")
    @ByteString
    val publicKey: ByteArray,

    @SerialName("t")
    private val validContentTypes: List<ContentType>,
) : TrustedCertificate {

    companion object {
        fun fromCert(certificate: X509Certificate) = TrustedCertificateV1(
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

    override fun getKid() = kid

    override fun getValidContentTypes() = validContentTypes

    override fun getValidFrom() = validFrom

    override fun getValidUntil() = validUntil

    override fun buildOneKey(): OneKey {
        val publicKey = when (keyType) {
            KeyType.RSA -> {
                val rsaPublicKey = org.bouncycastle.asn1.pkcs.RSAPublicKey.getInstance(publicKey)
                val spec = RSAPublicKeySpec(rsaPublicKey.modulus, rsaPublicKey.publicExponent)
                KeyFactory.getInstance("RSA").generatePublic(spec)
            }
            KeyType.EC -> {
                val ecCurveName = when (publicKey.size) {
                    65 -> SECNamedCurves.getName(SECObjectIdentifiers.secp256r1)
                    97 -> SECNamedCurves.getName(SECObjectIdentifiers.secp384r1)
                    else -> throw IllegalArgumentException("key")
                }
                val param = SECNamedCurves.getByName(ecCurveName)
                val paramSpec = ECNamedCurveSpec(ecCurveName, param.curve, param.g, param.n)
                val publicPoint = ECPointUtil.decodePoint(paramSpec.curve, publicKey)
                val spec = ECPublicKeySpec(publicPoint, paramSpec)
                KeyFactory.getInstance("EC").generatePublic(spec)
            }
        }
        return OneKey(publicKey, null)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrustedCertificateV1

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