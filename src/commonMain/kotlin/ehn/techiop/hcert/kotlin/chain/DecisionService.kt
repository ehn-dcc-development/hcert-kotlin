package ehn.techiop.hcert.kotlin.chain

import kotlinx.datetime.Clock

/**
 * Decides if the [VerificationResult] from [Chain.decode] was correct, i.e. it can be accepted.
 *
 * TODO Implement some national rules on the data?
 */
class DecisionService(private val clock: Clock = Clock.System) {

    fun decide(verificationResult: VerificationResult): VerificationDecision {
        for (content in verificationResult.content) {
            if (!verificationResult.certificateValidContent.contains(content))
                return VerificationDecision.FAIL.also {
                    verificationResult.error = VerificationResult.Error.UNSUITABLE_PUBLIC_KEY_TYPE
                }
        }

        return VerificationDecision.GOOD
    }

}
