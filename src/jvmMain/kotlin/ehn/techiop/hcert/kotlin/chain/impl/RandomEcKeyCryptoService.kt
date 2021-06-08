package ehn.techiop.hcert.kotlin.chain.impl

import COSE.OneKey
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.common.selfSignCertificate
import ehn.techiop.hcert.kotlin.crypto.Certificate
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CosePrivKey
import ehn.techiop.hcert.kotlin.crypto.CosePubKey
import ehn.techiop.hcert.kotlin.crypto.CwtAlgorithm
import ehn.techiop.hcert.kotlin.crypto.JvmCertificate
import ehn.techiop.hcert.kotlin.crypto.JvmPrivKey
import ehn.techiop.hcert.kotlin.crypto.JvmPubKey
import ehn.techiop.hcert.kotlin.crypto.PubKey
import ehn.techiop.hcert.kotlin.crypto.kid
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator
import org.bouncycastle.util.io.pem.PemWriter
import java.io.StringWriter
import java.security.KeyPairGenerator


actual class RandomEcKeyCryptoService actual constructor(
    val keySize: Int,
    contentType: List<ContentType>,
    clock: Clock
) : CryptoService {

    private val algorithm = when(keySize) {
        256 -> CwtAlgorithm.ECDSA_256
        384 -> CwtAlgorithm.ECDSA_384
        else -> throw IllegalArgumentException("keySize: $keySize")
    }
    private val keyPair = KeyPairGenerator.getInstance("EC")
        .apply { initialize(keySize) }.genKeyPair()
    private val certificate = selfSignCertificate(
        "EC-Me",
        JvmPrivKey(keyPair.private),
        JvmPubKey(keyPair.public),
        keySize,
        contentType,
        clock
    ) as JvmCertificate
    private val keyId = certificate.certificate.kid

    override fun getCborHeaders() = listOf(
        Pair(CoseHeaderKeys.ALGORITHM, algorithm),
        Pair(CoseHeaderKeys.KID, keyId)
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
        JcaPEMWriter(this).use { it.writeObject(certificate.certificate) }
    }.toString()

}


