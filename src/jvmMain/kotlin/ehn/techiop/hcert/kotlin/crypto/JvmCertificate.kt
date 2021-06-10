package ehn.techiop.hcert.kotlin.crypto

import COSE.OneKey
import ehn.techiop.hcert.kotlin.trust.ContentType
import ehn.techiop.hcert.kotlin.trust.Hash
import ehn.techiop.hcert.kotlin.trust.TrustedCertificateV2
import kotlinx.datetime.Instant
import java.security.cert.X509Certificate

class JvmCertificate(val certificate: X509Certificate) : CertificateAdapter<X509Certificate> {

    override val validContentTypes: List<ContentType>
        get() {
            val contentTypes = mutableSetOf<ContentType>()
            certificate.extendedKeyUsage?.let {
                it.forEach { oid ->
                    ContentType.findByOid(oid)?.let { contentTypes.add(it) }
                }
            }
            return contentTypes.ifEmpty { ContentType.values().toList() }.toList()
        }

    override val validFrom = Instant.fromEpochMilliseconds(certificate.notBefore.time)

    override val validUntil = Instant.fromEpochMilliseconds(certificate.notAfter.time)

    override val publicKey: PubKey<OneKey> = CosePubKey(OneKey(certificate.publicKey, null))

    override fun toTrustedCertificate() = TrustedCertificateV2(kid, certificate.encoded)

    override val kid = certificate.kid

    override val encoded = certificate.encoded

}


val X509Certificate.kid: ByteArray
    get() = Hash(encoded).calc().copyOf(8)

