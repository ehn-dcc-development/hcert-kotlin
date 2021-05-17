package ehn.techiop.hcert.kotlin.chain.impl

import COSE.HeaderKeys
import COSE.MessageTag
import COSE.OneKey
import COSE.Sign1Message
import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.trust.buildCosePublicKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

class VerificationCoseService(private val repository: CertificateRepository) : CoseService {

    init {
        Security.addProvider(BouncyCastleProvider()) // for SHA256withRSA/PSS
    }

    override fun encode(input: ByteArray) = throw NotImplementedError()

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.coseVerified = false
        return try {
            (Sign1Message.DecodeFromBytes(input, MessageTag.Sign1) as Sign1Message).also {
                try {
                    val kid = it.findAttribute(HeaderKeys.KID)?.GetByteString() ?: throw IllegalArgumentException("kid")
                    repository.loadTrustedCertificates(kid, verificationResult).forEach { trustedCert ->
                        verificationResult.certificateValidFrom = trustedCert.validFrom
                        verificationResult.certificateValidUntil = trustedCert.validUntil
                        verificationResult.certificateValidContent = trustedCert.validContentTypes
                        if (it.validate(trustedCert.buildCosePublicKey().toCoseRepresentation() as OneKey)) {
                            verificationResult.coseVerified = true
                            return it.GetContent()
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

}