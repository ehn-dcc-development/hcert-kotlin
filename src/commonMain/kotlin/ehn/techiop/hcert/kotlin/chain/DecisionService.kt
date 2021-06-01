package ehn.techiop.hcert.kotlin.chain

import kotlinx.datetime.Clock

/**
 * Decides if the [VerificationResult] from [Chain.decode] was correct, i.e. it can be accepted.
 *
 * TODO Implement some national rules on the data?
 */
class DecisionService(private val clock: Clock = Clock.System) {

    fun decide(verificationResult: VerificationResult): VerificationDecision {
        if (verificationResult.contextIdentifier == null)
            return VerificationDecision.FAIL

        if (!verificationResult.base45Decoded)
            return VerificationDecision.FAIL

        if (!verificationResult.coseVerified)
            return VerificationDecision.FAIL

        for (content in verificationResult.content) {
            if (!verificationResult.certificateValidContent.contains(content))
                return VerificationDecision.FAIL
        }

        if (!verificationResult.cwtDecoded)
            return VerificationDecision.FAIL

        if (!verificationResult.cborDecoded)
            return VerificationDecision.FAIL

        verificationResult.issuedAt?.let { issuedAt ->
            verificationResult.certificateValidFrom?.let { certValidFrom ->
                if (issuedAt < certValidFrom)
                    return VerificationDecision.FAIL
            }
            if (issuedAt > clock.now())
                return VerificationDecision.FAIL
        }

        verificationResult.expirationTime?.let { expirationTime ->
            verificationResult.certificateValidUntil?.let { certValidUntil ->
                if (expirationTime > certValidUntil)
                    return VerificationDecision.FAIL
            }
            if (expirationTime < clock.now())
                return VerificationDecision.FAIL
        }

        return VerificationDecision.GOOD
    }

}
