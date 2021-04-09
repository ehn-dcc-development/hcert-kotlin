package ehn.techiop.hcert.kotlin.chain

import COSE.AlgorithmID
import COSE.HeaderKeys
import COSE.KeyKeys
import COSE.OneKey
import com.upokecenter.cbor.CBORObject
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.asn1.x509.KeyUsage
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.io.ByteArrayInputStream
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.interfaces.ECPrivateKey
import java.util.Date
import java.util.Random

class RandomKeyCryptoService : CryptoService {

    private val keyPair = KeyPairGenerator.getInstance("EC")
        .apply { initialize(256) }.genKeyPair()
    private val keyPairCert: X509Certificate = selfSignCertificate(
        SubjectPublicKeyInfo.getInstance(ASN1Sequence.getInstance(keyPair.public.encoded)),
        X500Name("CN=Me")
    )

    private val keyId: String = MessageDigest.getInstance("SHA-256")
        .digest(keyPairCert.encoded)
        .copyOf(8).asBase64Url()

    override fun getCborHeaders() = listOf(
        Pair(HeaderKeys.Algorithm, AlgorithmID.ECDSA_256.AsCBOR()),
        Pair(HeaderKeys.KID, CBORObject.FromObject(keyId))
    )

    override fun getCborSigningKey() = OneKey(CBORObject.NewMap().also {
        it[KeyKeys.KeyType.AsCBOR()] = KeyKeys.KeyType_EC2
        it[KeyKeys.EC2_Curve.AsCBOR()] = KeyKeys.EC2_P256
        it[KeyKeys.EC2_D.AsCBOR()] = CBORObject.FromObject((keyPair.private as ECPrivateKey).s.toByteArray())
    })

    override fun getCborVerificationKey(kid: String): OneKey {
        if (kid != keyId) throw IllegalArgumentException("kid not known: $kid")
        val bcPublicKey = ECUtil.generatePublicKeyParameter(keyPair.public) as ECPublicKeyParameters
        return OneKey(CBORObject.NewMap().also {
            it[KeyKeys.KeyType.AsCBOR()] = KeyKeys.KeyType_EC2
            it[KeyKeys.EC2_Curve.AsCBOR()] = KeyKeys.EC2_P256
            it[KeyKeys.EC2_X.AsCBOR()] = CBORObject.FromObject((bcPublicKey).q.xCoord.encoded)
            it[KeyKeys.EC2_Y.AsCBOR()] = CBORObject.FromObject((bcPublicKey).q.yCoord.encoded)
        })
    }

    override fun getCertificate(kid: String): Certificate {
        if (kid != keyId) throw IllegalArgumentException("kid not known: $kid")
        return keyPairCert
    }

    private fun selfSignCertificate(
        subjectPublicKeyInfo: SubjectPublicKeyInfo,
        subjectName: X500Name
    ): X509Certificate {
        val keyUsage = KeyUsage(KeyUsage.digitalSignature or KeyUsage.keyEncipherment)
        val keyUsageExt = Extension.create(Extension.keyUsage, true, keyUsage)
        val notBefore = Date()
        val notAfter = Date(notBefore.time + 24L * 60L * 60L * 1000L)
        val serialNumber = BigInteger(32, Random()).abs()
        val builder = X509v3CertificateBuilder(
            subjectName, serialNumber, notBefore, notAfter, subjectName, subjectPublicKeyInfo
        )
        listOf(keyUsageExt).forEach<Extension> { builder.addExtension(it) }
        val contentSigner = JcaContentSignerBuilder("SHA256withECDSA").build(keyPair.private)
        val certificateHolder = builder.build(contentSigner)
        return CertificateFactory.getInstance("X.509")
            .generateCertificate(ByteArrayInputStream(certificateHolder.encoded)) as X509Certificate
    }

}


