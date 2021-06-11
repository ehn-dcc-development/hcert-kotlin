package ehn.techiop.hcert.kotlin.chain.common

import ehn.techiop.hcert.kotlin.crypto.*
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock
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
import java.security.PrivateKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.interfaces.ECPrivateKey
import java.util.Date
import java.util.Random
import kotlin.time.Duration

actual class PkiUtils {
    actual fun selfSignCertificate(
        commonName: String,
        privateKey: PrivKey,
        publicKey: PubKey,
        keySize: Int,
        contentType: List<ContentType>,
        clock: Clock
    ): CertificateAdapter {
        val publicKeyEncoded = (publicKey as JvmPubKey).toPlatformPublicKey().encoded
        val subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(ASN1Sequence.getInstance(publicKeyEncoded))
        val keyUsage = KeyUsage(KeyUsage.digitalSignature or KeyUsage.keyEncipherment)
        val keyUsageExt = Extension.create(Extension.keyUsage, true, keyUsage)
        val extendedKeyUsage = ExtendedKeyUsage(certTypeToKeyUsages(contentType))
        val testUsage = Extension.create(Extension.extendedKeyUsage, false, extendedKeyUsage)
        val notBefore = clock.now()
        val notAfter = notBefore.plus(Duration.days(30))
        val serialNumber = BigInteger(32, Random()).abs()
        val builder = X509v3CertificateBuilder(
            X500Name("CN=$commonName"),
            serialNumber,
            Date(notBefore.toEpochMilliseconds()),
            Date(notAfter.toEpochMilliseconds()),
            X500Name("CN=$commonName"),
            subjectPublicKeyInfo
        )
        listOf(keyUsageExt, testUsage).forEach<Extension> { builder.addExtension(it) }
        val jvmPrivateKey = (privateKey as JvmPrivKey).toPlatformPrivateKey()
        val contentSigner = JcaContentSignerBuilder(getAlgorithm(jvmPrivateKey)).build(jvmPrivateKey)
        val certificateHolder = builder.build(contentSigner)
        return CertificateAdapter(
            CertificateFactory.getInstance("X.509")
                .generateCertificate(ByteArrayInputStream(certificateHolder.encoded)) as X509Certificate
        )
    }

    private fun certTypeToKeyUsages(contentType: List<ContentType>) = contentType.map {
        KeyPurposeId.getInstance(ASN1ObjectIdentifier(it.oid))
    }.toTypedArray()

    private fun getAlgorithm(private: PrivateKey) = when (private) {
        is ECPrivateKey -> "SHA256withECDSA"
        else -> "SHA256withRSA"
    }

}