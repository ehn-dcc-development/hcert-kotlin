package ehn.techiop.hcert.kotlin.chain

import kotlinx.datetime.Clock

/**
 * Decides if the [VerificationResult] from [Chain.decode] was correct, i.e. it can be accepted.
 *
 * TODO Implement some national rules on the data?
 */
class DecisionService(private val clock: Clock = Clock.System) {

    fun decide(verificationResult: VerificationResult): VerificationDecision {
        return VerificationDecision.GOOD
    }

}
