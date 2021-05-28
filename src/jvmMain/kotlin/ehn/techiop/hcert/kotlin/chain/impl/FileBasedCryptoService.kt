package ehn.techiop.hcert.kotlin.chain.impl

import COSE.AlgorithmID
import COSE.OneKey
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import ehn.techiop.hcert.kotlin.crypto.Certificate
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CosePrivKey
import ehn.techiop.hcert.kotlin.crypto.CosePubKey
import ehn.techiop.hcert.kotlin.crypto.JvmCertificate
import ehn.techiop.hcert.kotlin.crypto.kid
import kotlinx.datetime.Instant
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

class FileBasedCryptoService(pemEncodedKeyPair: String, pemEncodedCertificate: String) : CryptoService {

    private val privateKey: PrivateKey
    private val publicKey: PublicKey
    private val algorithmID: AlgorithmID
    private val certificate: X509Certificate
    private val keyId: ByteArray

    init {
        Security.addProvider(BouncyCastleProvider())
        val read = PEMParser(pemEncodedKeyPair.reader()).readObject() as PrivateKeyInfo
        privateKey = JcaPEMKeyConverter().getPrivateKey(read)
        algorithmID = when (privateKey) {
            is ECPrivateKey -> AlgorithmID.ECDSA_256
            is RSAPrivateKey -> AlgorithmID.RSA_PSS_256
            else -> throw IllegalArgumentException("KeyType unknown")
        }
        certificate = CertificateFactory.getInstance("X.509")
            .generateCertificate(pemEncodedCertificate.byteInputStream()) as X509Certificate
        publicKey = certificate.publicKey
        keyId = certificate.kid
    }

    override fun getCborHeaders() = listOf(
        Pair(CoseHeaderKeys.Algorithm, algorithmID.AsCBOR()),
        Pair(CoseHeaderKeys.KID, CBORObject.FromObject(keyId))
    )

    override fun getCborSigningKey() = CosePrivKey(OneKey(publicKey, privateKey))

    override fun getCborVerificationKey(
        kid: ByteArray,
        verificationResult: VerificationResult
    ): ehn.techiop.hcert.kotlin.crypto.PubKey<*> {
        if (!(keyId contentEquals kid)) throw IllegalArgumentException("kid not known: $kid")
        verificationResult.certificateValidFrom =
            Instant.fromEpochSeconds(certificate.notBefore.toInstant().epochSecond)
        verificationResult.certificateValidUntil =
            Instant.fromEpochSeconds(certificate.notAfter.toInstant().epochSecond)
        verificationResult.certificateValidContent = PkiUtils.getValidContentTypes(certificate)
        return CosePubKey(OneKey(publicKey, privateKey))
    }

    override fun getCertificate(): Certificate<*> = JvmCertificate(certificate)

    override fun exportPrivateKeyAsPem() = StringWriter().apply {
        PemWriter(this).use {
            it.writeObject(JcaPKCS8Generator(privateKey, null).generate())
        }
    }.toString()

    override fun exportCertificateAsPem() = StringWriter().apply {
        JcaPEMWriter(this).use { it.writeObject(certificate) }
    }.toString()

}


