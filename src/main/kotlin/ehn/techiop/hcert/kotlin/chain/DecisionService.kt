package ehn.techiop.hcert.kotlin.chain

import java.time.Clock

/**
 * Decides if the [VerificationResult] from [Chain.decode] was correct, i.e. it can be accepted.
 *
 * TODO Implement some national rules on the data?
 */
class DecisionService(private val clock: Clock = Clock.systemUTC()) {

    fun decide(verificationResult: VerificationResult): VerificationDecision {
        if (!verificationResult.coseVerified)
            return VerificationDecision.FAIL.also {
                println("!verificationResult.coseVerified")
            }

        if (!verificationResult.base45Decoded)
            return VerificationDecision.FAIL.also {
                println("!verificationResult.base45Decoded")
            }

        if (!verificationResult.cwtDecoded)
            return VerificationDecision.FAIL.also {
                println("!verificationResult.cwtDecoded")
            }

        if (!verificationResult.cborDecoded)
            return VerificationDecision.FAIL.also {
                println("!verificationResult.cborDecoded")
            }

        if (verificationResult.contextIdentifier == null)
            return VerificationDecision.FAIL.also {
                println("verificationResult.contextIdentifier == null")
            }

        verificationResult.issuedAt?.let { issuedAt ->
            verificationResult.certificateValidFrom?.let { certValidFrom ->
                if (issuedAt.isBefore(certValidFrom))
                    return VerificationDecision.FAIL.also {
                        println("issuedAt.isBefore(certValidFrom)")
                    }
            }
            if (issuedAt.isAfter(clock.instant()))
                return VerificationDecision.FAIL.also {
                    println("issuedAt.isAfter(clock.instant())")
                }
        }

        verificationResult.expirationTime?.let { expirationTime ->
            verificationResult.certificateValidUntil?.let { certValidUntil ->
                if (expirationTime.isAfter(certValidUntil))
                    return VerificationDecision.FAIL.also {
                        println("expirationTime.isAfter(certValidUntil)")
                    }
            }
            if (expirationTime.isBefore(clock.instant()))
                return VerificationDecision.FAIL.also {
                    println("expirationTime.isBefore(clock.instant())")
                }
        }

        for (content in verificationResult.content) {
            if (!verificationResult.certificateValidContent.contains(content))
                return VerificationDecision.FAIL.also {
                    println("!verificationResult.certificateValidContent.contains(content)")
                }
        }

        return VerificationDecision.GOOD
    }

}
