package ehn.techiop.hcert.kotlin.chain.impl

import COSE.AlgorithmID
import COSE.OneKey
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import ehn.techiop.hcert.kotlin.crypto.*
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Instant
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator
import org.bouncycastle.util.io.pem.PemWriter
import java.io.StringWriter
import java.security.KeyPairGenerator
import java.time.Clock

class RandomEcKeyCryptoService(
    private val keySize: Int = 256,
    contentType: List<ContentType> = listOf(ContentType.TEST, ContentType.VACCINATION, ContentType.RECOVERY),
    clock: Clock = Clock.systemUTC(),
) : CryptoService {

    private val keyPair = KeyPairGenerator.getInstance("EC")
        .apply { initialize(keySize) }.genKeyPair()
    private val certificate = PkiUtils.selfSignCertificate(X500Name("CN=EC-Me"), keyPair, contentType, clock)
    private val keyId = certificate.kid
    private val algorithmId = when (keySize) {
        384 -> AlgorithmID.ECDSA_384
        256 -> AlgorithmID.ECDSA_256
        else -> throw IllegalArgumentException("keySize")
    }

    override fun getCborHeaders() = listOf(
        Pair(CoseHeaderKeys.Algorithm, algorithmId.AsCBOR()),
        Pair(CoseHeaderKeys.KID, CBORObject.FromObject(keyId))
    )

    override fun getCborSigningKey() = CosePrivateKey(OneKey(keyPair.public, keyPair.private))

    override fun getCborVerificationKey(kid: ByteArray, verificationResult: VerificationResult): PublicKey<*> {
        if (!(keyId contentEquals kid)) throw IllegalArgumentException("kid not known: $kid")
        verificationResult.certificateValidFrom = Instant.fromEpochSeconds(certificate.notBefore.toInstant().epochSecond)
        verificationResult.certificateValidUntil = Instant.fromEpochSeconds(certificate.notAfter.toInstant().epochSecond)
        verificationResult.certificateValidContent = PkiUtils.getValidContentTypes(certificate)
        return CosePubKey(OneKey(keyPair.public, keyPair.private))
    }

    override fun getCertificate(): Certificate<*> = JvmCertificate(certificate)

    override fun exportPrivateKeyAsPem() = StringWriter().apply {
        PemWriter(this).use {
            it.writeObject(JcaPKCS8Generator(keyPair.private, null).generate())
        }
    }.toString()

    override fun exportCertificateAsPem() = StringWriter().apply {
        JcaPEMWriter(this).use { it.writeObject(certificate) }
    }.toString()

}


