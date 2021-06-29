package ehn.techiop.hcert.kotlin.crypto

import ehn.techiop.hcert.kotlin.chain.fromBase64
import ehn.techiop.hcert.kotlin.trust.ContentType
import ehn.techiop.hcert.kotlin.trust.Hash
import ehn.techiop.hcert.kotlin.trust.TrustedCertificateV2
import kotlinx.datetime.Instant
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

actual class CertificateAdapter(val certificate: X509Certificate) {

    actual constructor(pemEncoded: String) : this(
        CertificateFactory.getInstance("X.509").generateCertificate(
            pemEncoded
                .replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .lines().joinToString(separator = "")
                .fromBase64().inputStream()
        ) as X509Certificate
    )

    actual constructor(_encoded: ByteArray) : this(
        CertificateFactory.getInstance("X.509")
            .generateCertificate(_encoded.inputStream()) as X509Certificate
    )

    actual val validContentTypes: List<ContentType>
        get() {
            val contentTypes = mutableSetOf<ContentType>()
            certificate.extendedKeyUsage?.let {
                it.forEach { oid ->
                    ContentType.findByOid(oid)?.let { contentTypes.add(it) }
                }
            }
            return contentTypes.ifEmpty { ContentType.values().toList() }.toList()
        }

    actual val validFrom = Instant.fromEpochMilliseconds(certificate.notBefore.time)

    actual val validUntil = Instant.fromEpochMilliseconds(certificate.notAfter.time)

    actual val subjectCountry = Regex("C=[^,]*").find(certificate.subjectX500Principal.name)?.value

    actual val publicKey: PubKey = JvmPubKey(certificate.publicKey)

    actual fun toTrustedCertificate() = TrustedCertificateV2(kid, certificate.encoded)

    actual val kid = certificate.kid

    actual val encoded = certificate.encoded

}

val X509Certificate.kid: ByteArray
    get() = Hash(encoded).calc().copyOf(8)

