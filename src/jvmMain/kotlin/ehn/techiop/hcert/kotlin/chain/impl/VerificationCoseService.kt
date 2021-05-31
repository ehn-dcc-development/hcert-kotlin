package ehn.techiop.hcert.kotlin.chain.impl

import COSE.HeaderKeys
import COSE.MessageTag
import COSE.OneKey
import COSE.Sign1Message
import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

actual class VerificationCoseService actual constructor(private val repository: CertificateRepository) : CoseService {

    init {
        Security.addProvider(BouncyCastleProvider()) // for SHA256withRSA/PSS
    }

    override fun encode(input: ByteArray) = throw NotImplementedError()

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.coseVerified = false
        val msg = Sign1Message.DecodeFromBytes(strippedInput(input), MessageTag.Sign1) as Sign1Message
        val kid = msg.findAttribute(HeaderKeys.KID)?.GetByteString() ?: throw IllegalArgumentException("kid")
        repository.loadTrustedCertificates(kid, verificationResult).forEach { trustedCert ->
            verificationResult.certificateValidFrom = trustedCert.validFrom
            verificationResult.certificateValidUntil = trustedCert.validUntil
            verificationResult.certificateValidContent = trustedCert.validContentTypes
            if (msg.validate(trustedCert.cosePublicKey.toCoseRepresentation() as OneKey)) {
                verificationResult.coseVerified = true
                return msg.GetContent()
            }
        }
        return msg.GetContent()

    }

    // Input may be tagged as a CWT and a Sign1
    private fun strippedInput(input: ByteArray): ByteArray {
        if (input.size >= 3 && input[0] == 0xD8.toByte() && input[1] == 0x3D.toByte() && input[2] == 0xD2.toByte())
            return input.drop(2).toByteArray()
        return input
    }

}