package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.KeyType
import ehn.techiop.hcert.kotlin.chain.TrustListService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec


class TrustListCertificateRepository(input: ByteArray, certificateRepository: CertificateRepository) :
    CertificateRepository {

    private val list = TrustListService(VerificationCryptoService(certificateRepository)).decode(input).certificates

    override fun loadPublicKey(kid: ByteArray, verificationResult: VerificationResult): PublicKey {
        val trustedCert = list.firstOrNull { it.kid contentEquals kid }
            ?: throw IllegalArgumentException("kid not known: $kid")
        val keyFactory = when (trustedCert.keyType) {
            KeyType.RSA -> KeyFactory.getInstance("RSA")
            KeyType.EC -> KeyFactory.getInstance("EC")
            else -> throw IllegalArgumentException("Unknown key type")
        }
        val publicKey = keyFactory.generatePublic(X509EncodedKeySpec(trustedCert.publicKey))
        verificationResult.certificateValidFrom = trustedCert.validFrom
        verificationResult.certificateValidUntil = trustedCert.validUntil
        return publicKey
    }

}
