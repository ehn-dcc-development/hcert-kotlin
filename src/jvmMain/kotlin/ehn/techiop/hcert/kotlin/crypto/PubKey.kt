package ehn.techiop.hcert.kotlin.crypto

import COSE.OneKey
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import ehn.techiop.hcert.kotlin.trust.ContentType
import ehn.techiop.hcert.kotlin.trust.TrustedCertificateV2
import kotlinx.datetime.Instant
import java.security.MessageDigest
import java.security.cert.X509Certificate

val X509Certificate.kid: ByteArray
    get() = MessageDigest.getInstance("SHA-256")
        .digest(encoded)
        .copyOf(8)

class CosePubKey(val oneKey: OneKey) : PublicKey<OneKey> {
    override fun toCoseRepresentation() = oneKey
}

class CosePrivateKey(val oneKey: OneKey) : PrivateKey<OneKey> {
    override fun toCoseRepresentation() = oneKey
}


class JvmCertificate(val certificate: X509Certificate) : Certificate<X509Certificate> {

    override fun getValidContentTypes(): List<ContentType> {
        return PkiUtils.getValidContentTypes(certificate)
    }

    override fun getValidFrom(): Instant {
        return Instant.fromEpochMilliseconds(certificate.notBefore.time)
    }

    override fun getValidUntil(): Instant {
        return Instant.fromEpochMilliseconds(certificate.notAfter.time)
    }

    override fun getPublicKey(): PublicKey<*> {
        return CosePubKey(OneKey(certificate.publicKey, null))
    }

    override fun toTrustedCertificate(): TrustedCertificateV2 {
        return TrustedCertificateV2(calcKid(), certificate.encoded)
    }

    override fun calcKid() = certificate.kid

}
