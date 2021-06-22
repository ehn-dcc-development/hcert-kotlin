package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.HigherOrderValidationService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.trust.ContentType

class DefaultHigherOrderValidationService:HigherOrderValidationService {
    override fun validate(schemaValidatedCertificate: GreenCertificate, verificationResult: VerificationResult): GreenCertificate? {

        try {
            if (schemaValidatedCertificate.tests?.filterNotNull()?.isNotEmpty() == true) {
                verificationResult.content.add(ContentType.TEST)
                if (!verificationResult.certificateValidContent.contains(ContentType.TEST)) {
                    throw Throwable("Type Test not valid").also {
                        verificationResult.error = Error.UNSUITABLE_PUBLIC_KEY_TYPE
                    }
                }
            }
            if (schemaValidatedCertificate.vaccinations?.filterNotNull()?.isNotEmpty() == true) {
                verificationResult.content.add(ContentType.VACCINATION)
                if (!verificationResult.certificateValidContent.contains(ContentType.VACCINATION)) {
                    throw Throwable("Type Vaccination not valid").also {
                        verificationResult.error = Error.UNSUITABLE_PUBLIC_KEY_TYPE
                    }
                }
            }
            if (schemaValidatedCertificate.recoveryStatements?.filterNotNull()?.isNotEmpty() == true) {
                verificationResult.content.add(ContentType.RECOVERY)
                if (!verificationResult.certificateValidContent.contains(ContentType.RECOVERY)) {
                    throw Throwable("Type Recovery not valid").also {
                        verificationResult.error = Error.UNSUITABLE_PUBLIC_KEY_TYPE
                    }
                }
            }
            return schemaValidatedCertificate
        } catch (e: Throwable) {
            throw e.also {
                verificationResult.error = Error.CBOR_DESERIALIZATION_FAILED
            }
        }
    }
}