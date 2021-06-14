package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CborService
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.fromHexString
import ehn.techiop.hcert.kotlin.chain.toHexString
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.serialization.SerializationException
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray

open class DefaultCborService : CborService {

    override fun encode(input: GreenCertificate) = Cbor.encodeToByteArray(input)

    override fun decode(input: ByteArray, verificationResult: VerificationResult): GreenCertificate? {
        try {
            val result = tagDecodingWorkaround(input)
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

    /**
     * kotlinx.serialization does not support CBOR tags.
     * But some member states may tag the LocalDate "sc" with CBOR Tag C0.
     * So we'll strip the C0 tag for this field.
     */
    private fun tagDecodingWorkaround(input: ByteArray): GreenCertificate {
        // TODO Remove "ignoreUnknownKeys", once everything is up to date with schema 1.2.1
        return try {
            Cbor { ignoreUnknownKeys = true }.decodeFromByteArray(input)
        } catch (e: SerializationException) {
            if (e.message?.contains("but found C0") == true) {
                val strippedInput = input.toHexString().uppercase()
                    .replace("627363C074", "62736374") // "sc"=C0(... 23 bytes
                    .replace("627363C078", "62736378") // "sc"=C0(... 27 bytes
                    .fromHexString()
                Cbor { ignoreUnknownKeys = true }.decodeFromByteArray(strippedInput)
            } else {
                throw e
            }
        }
    }
}