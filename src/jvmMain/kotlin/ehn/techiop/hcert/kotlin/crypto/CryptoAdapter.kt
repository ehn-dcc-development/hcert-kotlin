package ehn.techiop.hcert.kotlin.crypto


import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator
import java.security.KeyPairGenerator
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.RSAPrivateKey

actual class CryptoAdapter {

    actual val privateKey: PrivKey
    actual val publicKey: PubKey
    actual val algorithm: CwtAlgorithm
    actual val privateKeyEncoded: ByteArray
    actual val certificate: CertificateAdapter

    actual constructor(keyType: KeyType, keySize: Int, contentType: List<ContentType>, clock: Clock) {
        Security.addProvider(BouncyCastleProvider())
        val keyPair = when (keyType) {
            KeyType.EC -> KeyPairGenerator.getInstance("EC")
                .apply { initialize(keySize) }.genKeyPair()
            KeyType.RSA -> KeyPairGenerator.getInstance("RSA")
                .apply { initialize(keySize) }.genKeyPair()
        }
        privateKey = JvmPrivKey(keyPair.private)
        publicKey = JvmPubKey(keyPair.public)
        algorithm = when (keyType) {
            KeyType.EC -> if (keySize == 384) CwtAlgorithm.ECDSA_384 else CwtAlgorithm.ECDSA_256
            KeyType.RSA -> CwtAlgorithm.RSA_PSS_256
        }
        privateKeyEncoded = JcaPKCS8Generator(keyPair.private, null).generate().content
        certificate = when (keyType) {
            KeyType.EC -> PkiUtils.selfSignCertificate("EC-Me", privateKey, publicKey, keySize, contentType, clock)
            KeyType.RSA -> PkiUtils.selfSignCertificate("RSA-Me", privateKey, publicKey, keySize, contentType, clock)
        }
    }


    actual constructor(pemEncodedPrivateKey: String, pemEncodedCertificate: String) {
        Security.addProvider(BouncyCastleProvider())
        val read = PEMParser(pemEncodedPrivateKey.reader()).readObject() as PrivateKeyInfo
        val nativePrivateKey = JcaPEMKeyConverter().getPrivateKey(read)
        algorithm = when (nativePrivateKey) {
            is ECPrivateKey -> {
                when (nativePrivateKey.params.curve.field.fieldSize) {
                    256 -> CwtAlgorithm.ECDSA_256
                    384 -> CwtAlgorithm.ECDSA_384
                    else -> throw IllegalArgumentException("KeyType unknown")
                }
            }
            is RSAPrivateKey -> CwtAlgorithm.RSA_PSS_256
            else -> throw IllegalArgumentException("KeyType unknown")
        }
        val x509Certificate = CertificateFactory.getInstance("X.509")
            .generateCertificate(pemEncodedCertificate.byteInputStream()) as X509Certificate
        certificate = CertificateAdapter(x509Certificate)
        privateKey = JvmPrivKey(nativePrivateKey)
        publicKey = JvmPubKey(x509Certificate.publicKey)
        privateKeyEncoded = JcaPKCS8Generator(nativePrivateKey, null).generate().content
    }

}