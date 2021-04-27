package ehn.techiop.hcert.kotlin.chain.common

import ehn.techiop.hcert.kotlin.trust.ContentType
import org.bouncycastle.asn1.ASN1ObjectIdentifier
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.ExtendedKeyUsage
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.asn1.x509.KeyPurposeId
import org.bouncycastle.asn1.x509.KeyUsage
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.io.ByteArrayInputStream
import java.math.BigInteger
import java.security.KeyPair
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.interfaces.ECPrivateKey
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Random

class PkiUtils {

    companion object {
        fun calcKid(certificate: X509Certificate) = MessageDigest.getInstance("SHA-256")
            .digest(certificate.encoded)
            .copyOf(8)

        fun selfSignCertificate(
            subjectName: X500Name,
            keyPair: KeyPair,
            contentType: List<ContentType> = listOf(ContentType.TEST, ContentType.VACCINATION, ContentType.RECOVERY)
        ): X509Certificate {
            val subjectPublicKeyInfo =
                SubjectPublicKeyInfo.getInstance(ASN1Sequence.getInstance(keyPair.public.encoded))
            val keyUsage = KeyUsage(KeyUsage.digitalSignature or KeyUsage.keyEncipherment)
            val keyUsageExt = Extension.create(Extension.keyUsage, true, keyUsage)
            val extendedKeyUsage = ExtendedKeyUsage(certTypeToKeyUsages(contentType))
            val testUsage = Extension.create(Extension.extendedKeyUsage, false, extendedKeyUsage)
            val notBefore = Instant.now()
            val notAfter = notBefore.plus(30, ChronoUnit.DAYS)
            val serialNumber = BigInteger(32, Random()).abs()
            val builder = X509v3CertificateBuilder(
                subjectName,
                serialNumber,
                Date.from(notBefore),
                Date.from(notAfter),
                subjectName,
                subjectPublicKeyInfo
            )
            listOf(keyUsageExt, testUsage).forEach<Extension> { builder.addExtension(it) }
            val contentSigner = JcaContentSignerBuilder(getAlgorithm(keyPair.private)).build(keyPair.private)
            val certificateHolder = builder.build(contentSigner)
            return CertificateFactory.getInstance("X.509")
                .generateCertificate(ByteArrayInputStream(certificateHolder.encoded)) as X509Certificate
        }

        private fun certTypeToKeyUsages(contentType: List<ContentType>): Array<KeyPurposeId> {
            var result = arrayOf<KeyPurposeId>()
            if (contentType.contains(ContentType.TEST))
                result += KeyPurposeId.getInstance(ASN1ObjectIdentifier("1.3.6.1.4.1.0.1847.2021.1.1"))
            if (contentType.contains(ContentType.VACCINATION))
                result += KeyPurposeId.getInstance(ASN1ObjectIdentifier("1.3.6.1.4.1.0.1847.2021.1.2"))
            if (contentType.contains(ContentType.RECOVERY))
                result += KeyPurposeId.getInstance(ASN1ObjectIdentifier("1.3.6.1.4.1.0.1847.2021.1.3"))
            return result
        }

        fun getValidContentTypes(certificate: X509Certificate): List<ContentType> {
            val result = mutableListOf<ContentType>()
            if (hasOid(certificate, ASN1ObjectIdentifier("1.3.6.1.4.1.0.1847.2021.1.1")))
                result += ContentType.TEST
            if (hasOid(certificate, ASN1ObjectIdentifier("1.3.6.1.4.1.0.1847.2021.1.2")))
                result += ContentType.VACCINATION
            if (hasOid(certificate, ASN1ObjectIdentifier("1.3.6.1.4.1.0.1847.2021.1.3")))
                result += ContentType.RECOVERY
            return if (result.isEmpty()) listOf(ContentType.TEST, ContentType.VACCINATION, ContentType.RECOVERY) else result

        }

        private fun hasOid(certificate: X509Certificate, oid: ASN1ObjectIdentifier): Boolean {
            return certificate.extendedKeyUsage != null && certificate.extendedKeyUsage.any { oid.toString() == it }
        }

        private fun getAlgorithm(private: PrivateKey) = when (private) {
            is ECPrivateKey -> "SHA256withECDSA"
            else -> "SHA256withRSA"
        }
    }

}


