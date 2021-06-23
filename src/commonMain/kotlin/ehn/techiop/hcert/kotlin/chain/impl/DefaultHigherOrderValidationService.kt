package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.Error.SCHEMA_VALIDATION_FAILED
import ehn.techiop.hcert.kotlin.chain.HigherOrderValidationService
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.trust.ContentType

class DefaultHigherOrderValidationService : HigherOrderValidationService {

    override fun validate(input: GreenCertificate, verificationResult: VerificationResult): GreenCertificate {
        val numberNonNullTests = input.tests?.filterNotNull()?.size ?: 0
        if (numberNonNullTests > 0) {
            verificationResult.content.add(ContentType.TEST)
            if (!verificationResult.certificateValidContent.contains(ContentType.TEST))
                throw VerificationException(Error.UNSUITABLE_PUBLIC_KEY_TYPE, "Type Test not valid")
        }
        val numberNonNullVaccinations = input.vaccinations?.filterNotNull()?.size ?: 0
        if (numberNonNullVaccinations > 0) {
            if (numberNonNullTests > 0)
                throw VerificationException(SCHEMA_VALIDATION_FAILED, "Vaccination and test entry found")

            verificationResult.content.add(ContentType.VACCINATION)
            if (!verificationResult.certificateValidContent.contains(ContentType.VACCINATION))
                throw VerificationException(Error.UNSUITABLE_PUBLIC_KEY_TYPE, "Type Vaccination not valid")
        }
        val numberNonNullRecoveryStatements = input.recoveryStatements?.filterNotNull()?.size ?: 0
        if (numberNonNullRecoveryStatements > 0) {
            if (numberNonNullTests > 0)
                throw VerificationException(SCHEMA_VALIDATION_FAILED, "Recovery and test entry found")
            if (numberNonNullVaccinations > 0)
                throw VerificationException(SCHEMA_VALIDATION_FAILED, "Recovery and vaccination entry found")

            verificationResult.content.add(ContentType.RECOVERY)
            if (!verificationResult.certificateValidContent.contains(ContentType.RECOVERY))
                throw VerificationException(Error.UNSUITABLE_PUBLIC_KEY_TYPE, "Type Recovery not valid")
        }
        if (numberNonNullTests == 0 && numberNonNullVaccinations == 0 && numberNonNullRecoveryStatements == 0) {
            throw VerificationException(SCHEMA_VALIDATION_FAILED, "No test, vaccination, or recovery entry")
        }
        if (numberNonNullTests + numberNonNullVaccinations + numberNonNullRecoveryStatements > 1) {
            throw VerificationException(SCHEMA_VALIDATION_FAILED, "More than one test, vaccination, and recovery entry")
        }
        return input
    }
}
