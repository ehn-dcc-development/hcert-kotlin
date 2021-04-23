package ehn.techiop.hcert.kotlin.chain

import java.time.Instant

class DecisionService {

    fun decide(verificationResult: VerificationResult): VerificationDecision {
        if (!verificationResult.coseVerified)
            return VerificationDecision.FAIL

        if (!verificationResult.base45Decoded)
            return VerificationDecision.FAIL

        if (!verificationResult.cborDecoded)
            return VerificationDecision.FAIL

        verificationResult.issuedAt?.let { issuedAt ->
            verificationResult.certificateValidFrom?.let { certValidFrom ->
                if (issuedAt.isBefore(certValidFrom))
                    return VerificationDecision.FAIL
            }
            if (issuedAt.isAfter(Instant.now()))
                return VerificationDecision.FAIL
        }

        verificationResult.expirationTime?.let { expirationTime ->
            verificationResult.certificateValidUntil?.let { certValidUntil ->
                if (certValidUntil.isAfter(expirationTime))
                    return VerificationDecision.FAIL
            }
            if (expirationTime.isBefore(Instant.now()))
                return VerificationDecision.FAIL
        }

        return VerificationDecision.GOOD
    }

}


enum class VerificationDecision {
    GOOD,
    FAIL,
    WARNING
}
