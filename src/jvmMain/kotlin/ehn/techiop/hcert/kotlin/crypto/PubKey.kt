package ehn.techiop.hcert.kotlin.crypto

import COSE.OneKey
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Instant
import java.security.cert.X509Certificate

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

}
