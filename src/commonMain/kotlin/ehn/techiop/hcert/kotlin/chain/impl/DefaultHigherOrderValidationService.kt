package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.HigherOrderValidationService
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.trust.ContentType

class DefaultHigherOrderValidationService:HigherOrderValidationService {
    override fun validate(schemaValidatedCertificate: GreenCertificate, verificationResult: VerificationResult): GreenCertificate? {

        if (schemaValidatedCertificate.tests?.filterNotNull()?.isNotEmpty() == true) {
            verificationResult.content.add(ContentType.TEST)
            if (!verificationResult.certificateValidContent.contains(ContentType.TEST))
                throw VerificationException(Error.UNSUITABLE_PUBLIC_KEY_TYPE, "Type Test not valid")
        }

        if (schemaValidatedCertificate.vaccinations?.filterNotNull()?.isNotEmpty() == true) {
            verificationResult.content.add(ContentType.VACCINATION)
            if (!verificationResult.certificateValidContent.contains(ContentType.VACCINATION))
                throw VerificationException(Error.UNSUITABLE_PUBLIC_KEY_TYPE, "Type Vaccination not valid")
        }

        if (schemaValidatedCertificate.recoveryStatements?.filterNotNull()?.isNotEmpty() == true) {
            verificationResult.content.add(ContentType.RECOVERY)
            if (!verificationResult.certificateValidContent.contains(ContentType.RECOVERY))
                throw VerificationException(Error.UNSUITABLE_PUBLIC_KEY_TYPE, "Type Recovery not valid")
        }
        return schemaValidatedCertificate
    }
}
