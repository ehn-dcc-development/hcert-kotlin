package ehn.techiop.hcert.kotlin.crypto

import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator
import java.security.KeyPairGenerator
import java.security.Security

actual class CryptoAdapter actual constructor(
    keyType: KeyType,
    keySize: Int,
    contentType: List<ContentType>,
    clock: Clock
) {

    init {
        Security.addProvider(BouncyCastleProvider()) // for SHA256withRSA/PSS
    }

    private val keyPair = when (keyType) {
        KeyType.EC -> KeyPairGenerator.getInstance("EC")
            .apply { initialize(keySize) }.genKeyPair()
        KeyType.RSA -> KeyPairGenerator.getInstance("RSA")
            .apply { initialize(keySize) }.genKeyPair()
    }

    actual val privateKey: PrivKey = JvmPrivKey(keyPair.private)
    private val publicKey: PubKey = JvmPubKey(keyPair.public)
    actual val algorithm: CwtAlgorithm = when (keyType) {
        KeyType.EC -> if (keySize == 384) CwtAlgorithm.ECDSA_384 else CwtAlgorithm.ECDSA_256
        KeyType.RSA -> CwtAlgorithm.RSA_PSS_256
    }
    actual val privateKeyEncoded: ByteArray = JcaPKCS8Generator(keyPair.private, null).generate().content
    actual val certificate: CertificateAdapter = when (keyType) {
        KeyType.EC -> PkiUtils.selfSignCertificate("EC-Me", privateKey, publicKey, keySize, contentType, clock)
        KeyType.RSA -> PkiUtils.selfSignCertificate("RSA-Me", privateKey, publicKey, keySize, contentType, clock)
    }

}