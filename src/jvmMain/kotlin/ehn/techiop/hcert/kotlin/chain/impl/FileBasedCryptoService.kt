package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CwtAlgorithm
import ehn.techiop.hcert.kotlin.crypto.JvmPrivKey
import ehn.techiop.hcert.kotlin.crypto.JvmPubKey
import ehn.techiop.hcert.kotlin.crypto.PubKey
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator
import org.bouncycastle.util.io.pem.PemWriter
import java.io.StringWriter
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.RSAPrivateKey

actual class FileBasedCryptoService actual constructor(pemEncodedPrivateKey: String, pemEncodedCertificate: String) :
    CryptoService {

    private val privateKey: PrivateKey
    private val publicKey: PublicKey
    private val algorithmID: CwtAlgorithm
    private val certificate: CertificateAdapter
    private val keyId: ByteArray

    init {
        Security.addProvider(BouncyCastleProvider())
        val read = PEMParser(pemEncodedPrivateKey.reader()).readObject() as PrivateKeyInfo
        privateKey = JcaPEMKeyConverter().getPrivateKey(read)
        algorithmID = when (privateKey) {
            is ECPrivateKey -> {
                when (privateKey.params.curve.field.fieldSize) {
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
        publicKey = x509Certificate.publicKey
        keyId = certificate.kid
    }

    override fun getCborHeaders() = listOf(
        Pair(CoseHeaderKeys.ALGORITHM, algorithmID),
        Pair(CoseHeaderKeys.KID, keyId)
    )

    override fun getCborSigningKey() = JvmPrivKey(privateKey)

    override fun getCborVerificationKey(kid: ByteArray, verificationResult: VerificationResult): PubKey {
        if (!(keyId contentEquals kid))
            throw VerificationException(Error.KEY_NOT_IN_TRUST_LIST, "kid not known: $kid")

        verificationResult.setCertificateData(certificate)
        return JvmPubKey(publicKey)
    }

    override fun getCertificate(): CertificateAdapter = certificate

    override fun exportPrivateKeyAsPem() = StringWriter().apply {
        PemWriter(this).use {
            it.writeObject(JcaPKCS8Generator(privateKey, null).generate())
        }
    }.toString()

    override fun exportCertificateAsPem() = StringWriter().apply {
        JcaPEMWriter(this).use { it.writeObject(certificate.certificate) }
    }.toString()

}


