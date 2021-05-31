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
        verificationResult.cborDecoded = false
        try {
            // TODO Remove "ignoreUnknownKeys", once everything is up to date with schema 1.2.1
            val result = Cbor { ignoreUnknownKeys = true }.decodeFromByteArray<GreenCertificate>(input)
            verificationResult.cborDecoded = true
            if (result.tests?.filterNotNull()?.isNotEmpty() == true)
                verificationResult.content.add(ContentType.TEST)
            if (result.vaccinations?.filterNotNull()?.isNotEmpty() == true)
                verificationResult.content.add(ContentType.VACCINATION)
            if (result.recoveryStatements?.filterNotNull()?.isNotEmpty() == true)
                verificationResult.content.add(ContentType.RECOVERY)
            return result
        } catch (e: Throwable) {
            return null
        }
    }
}