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
            return VerificationDecision.FAIL.also {
                verificationResult.error = VerificationResult.Error.CONTEXT_IDENTIFIER_INVALID
            }

        if (!verificationResult.base45Decoded)
            return VerificationDecision.FAIL.also {
                verificationResult.error = VerificationResult.Error.BASE_45_DECODING_FAILED
            }

        if (!verificationResult.coseVerified)
            return VerificationDecision.FAIL.also {
                verificationResult.error = VerificationResult.Error.SIGNATURE_INVALID
            }

        for (content in verificationResult.content) {
            if (!verificationResult.certificateValidContent.contains(content))
                return VerificationDecision.FAIL.also {
                    verificationResult.error = VerificationResult.Error.UNSUITABLE_PUBLIC_KEY_TYPE
                }
        }

        if (!verificationResult.cwtDecoded)
            return VerificationDecision.FAIL.also {
                verificationResult.error = VerificationResult.Error.CBOR_DESERIALIZATION_FAILED
            }

        if (!verificationResult.cborDecoded)
            return VerificationDecision.FAIL.also {
                verificationResult.error = VerificationResult.Error.CBOR_DESERIALIZATION_FAILED
            }

        verificationResult.issuedAt?.let { issuedAt ->
            verificationResult.certificateValidFrom?.let { certValidFrom ->
                if (issuedAt < certValidFrom)
                    return VerificationDecision.FAIL.also {
                        verificationResult.error = VerificationResult.Error.CWT_EXPIRED
                    }
            }
            if (issuedAt > clock.now())
                return VerificationDecision.FAIL.also {
                    verificationResult.error = VerificationResult.Error.CWT_EXPIRED
                }
        }

        verificationResult.expirationTime?.let { expirationTime ->
            verificationResult.certificateValidUntil?.let { certValidUntil ->
                if (expirationTime > certValidUntil)
                    return VerificationDecision.FAIL.also {
                        verificationResult.error = VerificationResult.Error.CWT_EXPIRED
                    }
            }
            if (expirationTime < clock.now())
                return VerificationDecision.FAIL.also {
                    verificationResult.error = VerificationResult.Error.CWT_EXPIRED
                }
        }

        return VerificationDecision.GOOD
    }

}
