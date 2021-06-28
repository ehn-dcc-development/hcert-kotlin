package ehn.techiop.hcert.kotlin.crypto

import ehn.techiop.hcert.kotlin.chain.asBase64
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator
import java.security.KeyPairGenerator
import java.security.Security

actual class CryptoAdapter actual constructor(keyType: KeyType, keySize: Int) {

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
    actual val publicKey: PubKey = JvmPubKey(keyPair.public)
    actual val algorithm: CwtAlgorithm = when (keyType) {
        KeyType.EC -> if (keySize == 384) CwtAlgorithm.ECDSA_384 else CwtAlgorithm.ECDSA_256
        KeyType.RSA -> CwtAlgorithm.RSA_PSS_256
    }
    actual val privateKeyBase64: String = JcaPKCS8Generator(keyPair.private, null).generate().content.asBase64()

}