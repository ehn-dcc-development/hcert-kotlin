package ehn.techiop.hcert.kotlin.chain.impl

import COSE.AlgorithmID
import COSE.HeaderKeys
import COSE.OneKey
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CertType
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator
import org.bouncycastle.util.io.pem.PemWriter
import java.io.StringWriter
import java.security.KeyPairGenerator

class RandomEcKeyCryptoService(
    private val keySize: Int = 256,
    certType: List<CertType> = listOf(CertType.TEST, CertType.VACCINATION, CertType.RECOVERY)
) : CryptoService {

    private val keyPair = KeyPairGenerator.getInstance("EC")
        .apply { initialize(keySize) }.genKeyPair()
    private val certificate = PkiUtils.selfSignCertificate(X500Name("CN=EC-Me"), keyPair, certType)
    private val keyId = PkiUtils.calcKid(certificate)
    private val algorithmId = when (keySize) {
        384 -> AlgorithmID.ECDSA_384
        256 -> AlgorithmID.ECDSA_256
        else -> throw IllegalArgumentException("keySize")
    }

    override fun getCborHeaders() = listOf(
        Pair(HeaderKeys.Algorithm, algorithmId.AsCBOR()),
        Pair(HeaderKeys.KID, CBORObject.FromObject(keyId))
    )

    override fun getCborSigningKey() = OneKey(keyPair.public, keyPair.private)

    override fun getCborVerificationKey(kid: ByteArray, verificationResult: VerificationResult): OneKey {
        if (!(keyId contentEquals kid)) throw IllegalArgumentException("kid not known: $kid")
        verificationResult.certificateValidFrom = certificate.notBefore.toInstant()
        verificationResult.certificateValidUntil = certificate.notAfter.toInstant()
        verificationResult.certificateValidContent = PkiUtils.getValidContentTypes(certificate)
        return OneKey(keyPair.public, keyPair.private)
    }

    override fun getCertificate() = certificate

    override fun exportPrivateKeyAsPem() = StringWriter().apply {
        PemWriter(this).use {
            it.writeObject(JcaPKCS8Generator(keyPair.private, null).generate())
        }
    }.toString()

    override fun exportCertificateAsPem() = StringWriter().apply {
        JcaPEMWriter(this).use { it.writeObject(certificate) }
    }.toString()

}


