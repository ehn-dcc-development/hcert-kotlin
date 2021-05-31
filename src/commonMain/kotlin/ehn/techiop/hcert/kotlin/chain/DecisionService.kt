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
            return VerificationDecision.FAIL_QRCODE

        if (!verificationResult.base45Decoded)
            return VerificationDecision.FAIL_QRCODE

        if (!verificationResult.coseVerified)
            return VerificationDecision.FAIL_SIGNATURE

        for (content in verificationResult.content) {
            if (!verificationResult.certificateValidContent.contains(content))
                return VerificationDecision.FAIL_SIGNATURE
        }

        if (!verificationResult.cwtDecoded)
            return VerificationDecision.FAIL_SIGNATURE

        if (!verificationResult.cborDecoded)
            return VerificationDecision.FAIL_QRCODE

        verificationResult.issuedAt?.let { issuedAt ->
            verificationResult.certificateValidFrom?.let { certValidFrom ->
                if (issuedAt < certValidFrom)
                    return VerificationDecision.FAIL_VALIDITY
            }
            if (issuedAt > clock.now())
                return VerificationDecision.FAIL_VALIDITY
        }

        verificationResult.expirationTime?.let { expirationTime ->
            verificationResult.certificateValidUntil?.let { certValidUntil ->
                if (expirationTime > certValidUntil)
                    return VerificationDecision.FAIL_VALIDITY
            }
            if (expirationTime < clock.now())
                return VerificationDecision.FAIL_VALIDITY
        }

        return VerificationDecision.GOOD
    }

}
