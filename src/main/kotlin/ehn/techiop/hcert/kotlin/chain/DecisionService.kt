package ehn.techiop.hcert.kotlin.chain

import java.time.Clock

/**
 * Decides if the [VerificationResult] from [Chain.decode] was correct, i.e. it can be accepted.
 *
 * TODO Implement some national rules on the data?
 */
class DecisionService(private val clock: Clock = Clock.systemDefaultZone()) {

    fun decide(verificationResult: VerificationResult): VerificationDecision {
        if (!verificationResult.coseVerified)
            return VerificationDecision.FAIL

        if (!verificationResult.base45Decoded)
            return VerificationDecision.FAIL

        if (!verificationResult.cwtDecoded)
            return VerificationDecision.FAIL

        if (!verificationResult.cborDecoded)
            return VerificationDecision.FAIL

        if (verificationResult.contextIdentifier == null)
            return VerificationDecision.FAIL

        verificationResult.issuedAt?.let { issuedAt ->
            verificationResult.certificateValidFrom?.let { certValidFrom ->
                if (issuedAt.isBefore(certValidFrom))
                    return VerificationDecision.FAIL
            }
            if (issuedAt.isAfter(clock.instant()))
                return VerificationDecision.FAIL
        }

        verificationResult.expirationTime?.let { expirationTime ->
            verificationResult.certificateValidUntil?.let { certValidUntil ->
                if (expirationTime.isAfter(certValidUntil))
                    return VerificationDecision.FAIL
            }
            if (expirationTime.isBefore(clock.instant()))
                return VerificationDecision.FAIL
        }

        for (content in verificationResult.content) {
            if (!verificationResult.certificateValidContent.contains(content))
                return VerificationDecision.FAIL
        }

        return VerificationDecision.GOOD
    }

}
