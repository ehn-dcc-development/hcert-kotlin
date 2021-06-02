package ehn.techiop.hcert.kotlin.crypto

import COSE.OneKey
import ehn.techiop.hcert.kotlin.trust.ContentType
import ehn.techiop.hcert.kotlin.trust.Hash
import ehn.techiop.hcert.kotlin.trust.TrustedCertificateV2
import kotlinx.datetime.Instant
import org.bouncycastle.asn1.ASN1ObjectIdentifier
import java.security.PrivateKey
import java.security.PublicKey
import java.security.cert.X509Certificate

val X509Certificate.kid: ByteArray
    get() = Hash(encoded).calc().copyOf(8)

class CosePubKey(val oneKey: OneKey) : PubKey<OneKey> {
    override fun toCoseRepresentation() = oneKey
}

class JvmPubKey(val publicKey: PublicKey) : PubKey<PublicKey> {
    override fun toCoseRepresentation() = publicKey
}

class CosePrivKey(val oneKey: OneKey) : PrivKey<OneKey> {
    override fun toCoseRepresentation() = oneKey
}

class JvmPrivKey(val privateKey: PrivateKey) : PrivKey<PrivateKey> {
    override fun toCoseRepresentation() = privateKey
}

class JvmCertificate(val certificate: X509Certificate) : Certificate<X509Certificate> {

    override val validContentTypes =
        ContentType.values().filter { hasOid(certificate, ASN1ObjectIdentifier(it.oid)) }
            .ifEmpty { ContentType.values().toList() }

    private fun hasOid(certificate: X509Certificate, oid: ASN1ObjectIdentifier): Boolean {
        return certificate.extendedKeyUsage != null && certificate.extendedKeyUsage.any { oid.toString() == it }
    }

    override val validFrom = Instant.fromEpochMilliseconds(certificate.notBefore.time)

    override val validUntil = Instant.fromEpochMilliseconds(certificate.notAfter.time)

    override val publicKey: PubKey<OneKey> = CosePubKey(OneKey(certificate.publicKey, null))

    override fun toTrustedCertificate() = TrustedCertificateV2(kid, certificate.encoded)

    override val kid = certificate.kid
}
