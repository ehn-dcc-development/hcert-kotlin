package ehn.techiop.hcert.kotlin.crypto

import COSE.OneKey
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import ehn.techiop.hcert.kotlin.trust.TrustedCertificateV2
import kotlinx.datetime.Instant
import java.security.MessageDigest
import java.security.cert.X509Certificate

val X509Certificate.kid: ByteArray
    get() = MessageDigest.getInstance("SHA-256")
        .digest(encoded)
        .copyOf(8)

class CosePubKey(val oneKey: OneKey) : PubKey<OneKey> {
    override fun toCoseRepresentation() = oneKey
}

class CosePrivKey(val oneKey: OneKey) : PrivKey<OneKey> {
    override fun toCoseRepresentation() = oneKey
}


class JvmCertificate(val certificate: X509Certificate) : Certificate<X509Certificate> {

    override val validContentTypes = PkiUtils.getValidContentTypes(certificate)

    override val validFrom = Instant.fromEpochMilliseconds(certificate.notBefore.time)

    override val validUntil = Instant.fromEpochMilliseconds(certificate.notAfter.time)

    override val publicKey: PubKey<*> = CosePubKey(OneKey(certificate.publicKey, null))

    override fun toTrustedCertificate() = TrustedCertificateV2(kid, certificate.encoded)

    override val kid = certificate.kid
}
