package ehn.techiop.hcert.kotlin.chain.impl

import COSE.AlgorithmID
import COSE.OneKey
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import ehn.techiop.hcert.kotlin.crypto.Certificate
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CosePrivKey
import ehn.techiop.hcert.kotlin.crypto.CosePubKey
import ehn.techiop.hcert.kotlin.crypto.JvmCertificate
import ehn.techiop.hcert.kotlin.crypto.PubKey
import ehn.techiop.hcert.kotlin.crypto.kid
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator
import org.bouncycastle.util.io.pem.PemWriter
import java.io.StringWriter
import java.security.KeyPairGenerator


class RandomEcKeyCryptoService(
    val keySize: Int = 256,
    contentType: List<ContentType> = ContentType.values().toList(),
    clock: Clock = Clock.System
) : CryptoService {

    private val keyPair = KeyPairGenerator.getInstance("EC")
        .apply { initialize(keySize) }.genKeyPair()
    private val x509Certificate = PkiUtils.selfSignCertificate(X500Name("CN=EC-Me"), keyPair, contentType, clock)
    private val certificate = JvmCertificate(x509Certificate)
    private val keyId = x509Certificate.kid
    private val algorithmId = when (keySize) {
        384 -> AlgorithmID.ECDSA_384
        256 -> AlgorithmID.ECDSA_256
        else -> throw IllegalArgumentException("keySize")
    }

    override fun getCborHeaders() = listOf(
        Pair(CoseHeaderKeys.Algorithm, algorithmId.AsCBOR()),
        Pair(CoseHeaderKeys.KID, CBORObject.FromObject(keyId))
    )

    override fun getCborSigningKey() = CosePrivKey(OneKey(keyPair.public, keyPair.private))

    override fun getCborVerificationKey(kid: ByteArray, verificationResult: VerificationResult): PubKey<*> {
        if (!(keyId contentEquals kid)) throw IllegalArgumentException("kid not known: $kid").also {
            verificationResult.error = Error.KEY_NOT_IN_TRUST_LIST
        }
        verificationResult.certificateValidFrom = certificate.validFrom
        verificationResult.certificateValidUntil = certificate.validUntil
        verificationResult.certificateValidContent = certificate.validContentTypes
        return CosePubKey(OneKey(keyPair.public, keyPair.private))
    }

    override fun getCertificate(): Certificate<*> = certificate

    override fun exportPrivateKeyAsPem() = StringWriter().apply {
        PemWriter(this).use {
            it.writeObject(JcaPKCS8Generator(keyPair.private, null).generate())
        }
    }.toString()

    override fun exportCertificateAsPem() = StringWriter().apply {
        JcaPEMWriter(this).use { it.writeObject(x509Certificate) }
    }.toString()

}


