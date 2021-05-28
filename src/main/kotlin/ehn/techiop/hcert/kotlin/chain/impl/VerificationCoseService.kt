package ehn.techiop.hcert.kotlin.chain.impl

import COSE.HeaderKeys
import COSE.MessageTag
import COSE.Sign1Message
import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
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
            (Sign1Message.DecodeFromBytes(strippedInput(input), MessageTag.Sign1) as Sign1Message).also {
                try {
                    val kid = it.findAttribute(HeaderKeys.KID)?.GetByteString() ?: throw IllegalArgumentException("kid")
                    repository.loadTrustedCertificates(kid, verificationResult).forEach { trustedCert ->
                        verificationResult.certificateValidFrom = trustedCert.getValidFrom()
                        verificationResult.certificateValidUntil = trustedCert.getValidUntil()
                        verificationResult.certificateValidContent = trustedCert.getValidContentTypes()
                        if (it.validate(trustedCert.buildOneKey())) {
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

    // Input may be tagged as a CWT and a Sign1
    private fun strippedInput(input: ByteArray): ByteArray {
        if (input.size >= 3 && input[0] == 0xD8.toByte() && input[1] == 0x3D.toByte() && input[2] == 0xD2.toByte())
            return input.drop(2).toByteArray()
        return input
    }
}