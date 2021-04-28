package ehn.techiop.hcert.kotlin.chain

import java.time.Instant

/**
 * Decides if the [VerificationResult] from [Chain.decode] was correct, i.e. it can be accepted.
 *
 * TODO Implement some national rules on the data?
 */
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
                if (expirationTime.isAfter(certValidUntil))
                    return VerificationDecision.FAIL
            }
            if (expirationTime.isBefore(Instant.now()))
                return VerificationDecision.FAIL
        }

        for (content in verificationResult.content) {
            if (!verificationResult.certificateValidContent.contains(content))
                return VerificationDecision.FAIL
        }

        return VerificationDecision.GOOD
    }

}
