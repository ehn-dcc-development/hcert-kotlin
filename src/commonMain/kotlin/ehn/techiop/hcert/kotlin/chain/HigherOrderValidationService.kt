package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.data.GreenCertificate

/**
 * Performs higher-level checks on deserialised GreenCertificate
 */
interface HigherOrderValidationService {

    fun validate(input: GreenCertificate, verificationResult: VerificationResult): GreenCertificate

}
