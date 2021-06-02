package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CborService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray

open class DefaultCborService : CborService {

    override fun encode(input: GreenCertificate) = Cbor.encodeToByteArray(input)

    override fun decode(input: ByteArray, verificationResult: VerificationResult): GreenCertificate? {
        try {
            // TODO Remove "ignoreUnknownKeys", once everything is up to date with schema 1.2.1
            val result = Cbor { ignoreUnknownKeys = true }.decodeFromByteArray<GreenCertificate>(input)
            if (result.tests?.filterNotNull()?.isNotEmpty() == true) {
                verificationResult.content.add(ContentType.TEST)
                if (!verificationResult.certificateValidContent.contains(ContentType.TEST)) {
                    throw Throwable("Type Test not valid").also {
                        verificationResult.error = VerificationResult.Error.UNSUITABLE_PUBLIC_KEY_TYPE
                    }
                }
            }
            if (result.vaccinations?.filterNotNull()?.isNotEmpty() == true) {
                verificationResult.content.add(ContentType.VACCINATION)
                if (!verificationResult.certificateValidContent.contains(ContentType.VACCINATION)) {
                    throw Throwable("Type Vaccination not valid").also {
                        verificationResult.error = VerificationResult.Error.UNSUITABLE_PUBLIC_KEY_TYPE
                    }
                }
            }
            if (result.recoveryStatements?.filterNotNull()?.isNotEmpty() == true) {
                verificationResult.content.add(ContentType.RECOVERY)
                if (!verificationResult.certificateValidContent.contains(ContentType.RECOVERY)) {
                    throw Throwable("Type Recovery not valid").also {
                        verificationResult.error = VerificationResult.Error.UNSUITABLE_PUBLIC_KEY_TYPE
                    }
                }
            }
            return result
        } catch (e: Throwable) {
            throw e.also {
                if (verificationResult.error == null)
                    verificationResult.error = VerificationResult.Error.CBOR_DESERIALIZATION_FAILED
            }
        }
    }
}