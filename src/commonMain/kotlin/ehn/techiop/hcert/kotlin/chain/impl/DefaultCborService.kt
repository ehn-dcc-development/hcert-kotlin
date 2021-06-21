package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CborService
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray

open class DefaultCborService : CborService {

    override fun encode(input: GreenCertificate) = Cbor.encodeToByteArray(input)

    override fun decode(input: ByteArray, verificationResult: VerificationResult): GreenCertificate? {
        val result = try {
            // TODO Remove "ignoreUnknownKeys", once everything is up to date with schema 1.2.1
            Cbor { ignoreUnknownKeys = true }.decodeFromByteArray<GreenCertificate>(input)
        } catch (e: Throwable) {
            throw VerificationException(Error.CBOR_DESERIALIZATION_FAILED, e.message, e)
        }

        if (result.tests?.filterNotNull()?.isNotEmpty() == true) {
            verificationResult.content.add(ContentType.TEST)
            if (!verificationResult.certificateValidContent.contains(ContentType.TEST))
                throw VerificationException(Error.UNSUITABLE_PUBLIC_KEY_TYPE, "Type Test not valid")
        }

        if (result.vaccinations?.filterNotNull()?.isNotEmpty() == true) {
            verificationResult.content.add(ContentType.VACCINATION)
            if (!verificationResult.certificateValidContent.contains(ContentType.VACCINATION))
                throw VerificationException(Error.UNSUITABLE_PUBLIC_KEY_TYPE, "Type Vaccination not valid")
        }

        if (result.recoveryStatements?.filterNotNull()?.isNotEmpty() == true) {
            verificationResult.content.add(ContentType.RECOVERY)
            if (!verificationResult.certificateValidContent.contains(ContentType.RECOVERY))
                throw VerificationException(Error.UNSUITABLE_PUBLIC_KEY_TYPE, "Type Recovery not valid")
        }

        return result
    }
}
