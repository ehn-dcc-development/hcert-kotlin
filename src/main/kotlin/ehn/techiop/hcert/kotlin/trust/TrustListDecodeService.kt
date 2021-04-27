package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import java.time.Instant

class TrustListDecodeService(private val coseService: CoseService) {

    fun decode(input: ByteArray): TrustList {
        val verificationResult = VerificationResult()
        val payload = coseService.decode(input, verificationResult)
        if (!verificationResult.coseVerified)
            throw IllegalArgumentException("signature")

        val trustList = Cbor.decodeFromByteArray<TrustList>(payload)

        if (trustList.validFrom.isAfter(Instant.now()))
            throw IllegalArgumentException("ValidFrom")
        if (trustList.validUntil.isBefore(Instant.now()))
            throw IllegalArgumentException("ValidUntil")

        return trustList
    }

}