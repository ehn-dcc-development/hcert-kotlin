package ehn.techiop.hcert.kotlin.chain.debug

import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.chain.Error.SCHEMA_VALIDATION_FAILED
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.trust.ContentType

class DebugHigherOrderValidationService : HigherOrderValidationService {

    override fun validate(input: GreenCertificate, verificationResult: VerificationResult): GreenCertificate {
        val numberNonNullTests = input.tests?.filterNotNull()?.size ?: 0
        if (numberNonNullTests > 0) {
            verificationResult.content.add(ContentType.TEST)
            if (verificationResult.certificateValidContent.isNotEmpty() && !verificationResult.certificateValidContent.contains(ContentType.TEST))
                throw NonFatalVerificationException(input,
                    Error.UNSUITABLE_PUBLIC_KEY_TYPE,
                    "Type Test not valid",
                    details = mapOf(
                        "ContentType" to ContentType.TEST.oid,
                        "certificateValidContent" to verificationResult.certificateValidContent.joinToString { it.oid }
                    )
                )
        }
        val numberNonNullVaccinations = input.vaccinations?.filterNotNull()?.size ?: 0
        if (numberNonNullVaccinations > 0) {
            if (numberNonNullTests > 0)
                throw NonFatalVerificationException(
                    input,
                    SCHEMA_VALIDATION_FAILED,
                    "Vaccination and test entry found",
                    details = mapOf("conflictingEntryTypes" to "['TEST','VACCINATION']")
                )

            verificationResult.content.add(ContentType.VACCINATION)
            if (verificationResult.certificateValidContent.isNotEmpty() && !verificationResult.certificateValidContent.contains(ContentType.VACCINATION))
                throw NonFatalVerificationException(input,
                    Error.UNSUITABLE_PUBLIC_KEY_TYPE,
                    "Type Vaccination not valid",
                    details = mapOf(
                        "ContentType" to ContentType.VACCINATION.oid,
                        "certificateValidContent" to verificationResult.certificateValidContent.joinToString { it.oid }
                    ))
        }
        val numberNonNullRecoveryStatements = input.recoveryStatements?.filterNotNull()?.size ?: 0
        if (numberNonNullRecoveryStatements > 0) {
            if (numberNonNullTests > 0)
                throw NonFatalVerificationException(
                    input,
                    SCHEMA_VALIDATION_FAILED, "Recovery and test entry found",
                    details = mapOf("conflictingEntryTypes" to "['TEST','RECOVERY']")
                )
            if (numberNonNullVaccinations > 0)
                throw VerificationException(
                    SCHEMA_VALIDATION_FAILED, "Recovery and vaccination entry found",
                    details = mapOf("conflictingEntryTypes" to "['RECOVERY','VACCINATION']")
                )

            verificationResult.content.add(ContentType.RECOVERY)
            if (verificationResult.certificateValidContent.isNotEmpty() && !verificationResult.certificateValidContent.contains(ContentType.RECOVERY))
                throw NonFatalVerificationException(input, Error.UNSUITABLE_PUBLIC_KEY_TYPE, "Type Recovery not valid",
                    details = mapOf(
                        "ContentType" to ContentType.RECOVERY.oid,
                        "certificateValidContent" to verificationResult.certificateValidContent.joinToString { it.oid }
                    ))
        }
        val numberExemptions = input.vaccinationExemption?.filterNotNull()?.size ?: 0
        if (numberExemptions > 0) {
            if (verificationResult.certificateValidContent.contains(ContentType.VACCINATION) || verificationResult.certificateValidContent.contains(
                    ContentType.RECOVERY
                ) || verificationResult.certificateValidContent.contains(ContentType.TEST)
            )
                throw NonFatalVerificationException(input, Error.UNSUITABLE_PUBLIC_KEY_TYPE, "Type Vaccination not valid",
                    details = mapOf(
                        "ContentType" to "Exemption",
                        "certificateValidContent" to verificationResult.certificateValidContent.joinToString { it.oid }
                    ))
        }

        if (numberExemptions == 0 && numberNonNullTests == 0 && numberNonNullVaccinations == 0 && numberNonNullRecoveryStatements == 0) {
            throw VerificationException(SCHEMA_VALIDATION_FAILED, "No test, vaccination, exemption, or recovery entry")
        }
        if (numberExemptions + numberNonNullTests + numberNonNullVaccinations + numberNonNullRecoveryStatements > 1) {
            throw VerificationException(SCHEMA_VALIDATION_FAILED, "More than one test, vaccination, exemption, and recovery entry")
        }
        return input
    }
}
