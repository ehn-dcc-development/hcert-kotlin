package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CborService
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.data.CborObject
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray

open class DefaultCborService : CborService {

    override fun encode(input: GreenCertificate) = Cbor.encodeToByteArray(input)

    override fun decode(input: CborObject, verificationResult: VerificationResult): GreenCertificate? {

        val result = DefaultSchemaValidationService().validate(input, verificationResult)
        try {
            if (result.tests?.filterNotNull()?.isNotEmpty() == true) {
                verificationResult.content.add(ContentType.TEST)
                if (!verificationResult.certificateValidContent.contains(ContentType.TEST)) {
                    throw Throwable("Type Test not valid").also {
                        verificationResult.error = Error.UNSUITABLE_PUBLIC_KEY_TYPE
                    }
                }
            }
            if (result.vaccinations?.filterNotNull()?.isNotEmpty() == true) {
                verificationResult.content.add(ContentType.VACCINATION)
                if (!verificationResult.certificateValidContent.contains(ContentType.VACCINATION)) {
                    throw Throwable("Type Vaccination not valid").also {
                        verificationResult.error = Error.UNSUITABLE_PUBLIC_KEY_TYPE
                    }
                }
            }
            if (result.recoveryStatements?.filterNotNull()?.isNotEmpty() == true) {
                verificationResult.content.add(ContentType.RECOVERY)
                if (!verificationResult.certificateValidContent.contains(ContentType.RECOVERY)) {
                    throw Throwable("Type Recovery not valid").also {
                        verificationResult.error = Error.UNSUITABLE_PUBLIC_KEY_TYPE
                    }
                }
            }
            return result
        } catch (e: Throwable) {
            throw e.also {
                verificationResult.error = Error.CBOR_DESERIALIZATION_FAILED
            }
        }
    }
}
