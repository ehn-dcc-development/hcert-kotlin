package ehn.techiop.hcert.kotlin.chain.impl

import COSE.MessageTag
import COSE.Sign1Message
import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.VerificationResult

class VerificationCoseService(private val repository: CertificateRepository) : CoseService {

    override fun encode(input: ByteArray) = throw NotImplementedError()

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.coseVerified = false
        return try {
            (Sign1Message.DecodeFromBytes(input, MessageTag.Sign1) as Sign1Message).also {
                try {
                    DefaultCoseService.getKid(it)?.let { kid ->
                        repository.loadTrustedCertificates(kid, verificationResult).forEach { trustedCert ->
                            verificationResult.certificateValidFrom = trustedCert.validFrom
                            verificationResult.certificateValidUntil = trustedCert.validUntil
                            verificationResult.certificateValidContent = trustedCert.validContentTypes
                            if (it.validate(trustedCert.buildOneKey())) {
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

}