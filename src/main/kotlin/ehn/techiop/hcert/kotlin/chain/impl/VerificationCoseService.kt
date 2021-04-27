package ehn.techiop.hcert.kotlin.chain.impl

import COSE.HeaderKeys
import COSE.MessageTag
import COSE.OneKey
import COSE.Sign1Message
import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.trust.KeyType
import ehn.techiop.hcert.kotlin.trust.TrustedCertificate
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec

class VerificationCoseService(private val repository: CertificateRepository) : CoseService {

    override fun encode(input: ByteArray) = throw NotImplementedError()

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.coseVerified = false
        return try {
            (Sign1Message.DecodeFromBytes(input, MessageTag.Sign1) as Sign1Message).also {
                try {
                    getKid(it)?.let { kid ->
                        repository.loadTrustedCertificates(kid, verificationResult).forEach { trustedCert ->
                            verificationResult.certificateValidFrom = trustedCert.validFrom
                            verificationResult.certificateValidUntil = trustedCert.validUntil
                            verificationResult.certificateValidContent = trustedCert.validContentTypes
                            val buildOneKey = buildOneKey(trustedCert)
                            if (it.validate(buildOneKey)) {
                                verificationResult.coseVerified = true
                                return it.GetContent()
                            }
                        }
                    }
                } catch (e: Throwable) {
                    it.GetContent()
                }
            }.GetContent()
        } catch (e: Throwable) {
            input
        }
    }

    private fun buildOneKey(trustedCert: TrustedCertificate): OneKey {
        val keyFactory = when (trustedCert.keyType) {
            KeyType.RSA -> KeyFactory.getInstance("RSA")
            KeyType.EC -> KeyFactory.getInstance("EC")
        }
        val publicKey = keyFactory.generatePublic(X509EncodedKeySpec(trustedCert.publicKey))
        return OneKey(publicKey, null)
    }

    companion object {
        fun getKid(it: Sign1Message): ByteArray? {
            val key = HeaderKeys.KID.AsCBOR()
            if (it.protectedAttributes.ContainsKey(key)) {
                return it.protectedAttributes.get(key).GetByteString()
            } else if (it.unprotectedAttributes.ContainsKey(key)) {
                return it.unprotectedAttributes.get(key).GetByteString()
            }
            return null
        }
    }

}