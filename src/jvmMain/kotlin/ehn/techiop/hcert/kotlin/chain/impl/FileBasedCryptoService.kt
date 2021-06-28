package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.crypto.CwtAlgorithm
import ehn.techiop.hcert.kotlin.crypto.JvmPrivKey
import ehn.techiop.hcert.kotlin.crypto.JvmPubKey
import ehn.techiop.hcert.kotlin.crypto.PrivKey
import ehn.techiop.hcert.kotlin.crypto.PubKey
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator
import java.security.PrivateKey
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.RSAPrivateKey

actual class LoadedCryptoAdapter actual constructor(pemEncodedPrivateKey: String, pemEncodedCertificate: String) {

    private val jvmPrivKey: PrivateKey
    actual val privateKey: PrivKey
    actual val algorithmID: CwtAlgorithm
    actual val certificate: CertificateAdapter

    init {
        Security.addProvider(BouncyCastleProvider())
        val read = PEMParser(pemEncodedPrivateKey.reader()).readObject() as PrivateKeyInfo
        jvmPrivKey = JcaPEMKeyConverter().getPrivateKey(read)
        algorithmID = when (jvmPrivKey) {
            is ECPrivateKey -> {
                when (jvmPrivKey.params.curve.field.fieldSize) {
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
        privateKey = JvmPrivKey(jvmPrivKey)
    }

    actual val exportPrivateKey: ByteArray = JcaPKCS8Generator(jvmPrivKey, null).generate().content
    actual val exportCertificate: ByteArray = certificate.encoded

}


