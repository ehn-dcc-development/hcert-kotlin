package ehn.techiop.hcert.kotlin.trust

import COSE.HeaderKeys
import COSE.MessageTag
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import java.time.Instant

class TrustListDecodeService(private val repository: CertificateRepository) {

    fun decode(input: ByteArray): TrustList {
        val sign1Message = Sign1Message.DecodeFromBytes(input, MessageTag.Sign1) as Sign1Message
        val kid = sign1Message.protectedAttributes[HeaderKeys.KID.AsCBOR()].GetByteString()
            ?: throw IllegalArgumentException("kid")

        val validated = validate(sign1Message, kid)
        if (!validated) throw IllegalArgumentException("signature")

        val version = sign1Message.protectedAttributes[CBORObject.FromObject(42)].AsInt32()
        if (version != 1) throw IllegalArgumentException("version")

        val payload = sign1Message.GetContent()
        val trustList = Cbor.decodeFromByteArray<TrustList>(payload)

        if (trustList.validFrom.isAfter(Instant.now()))
            throw IllegalArgumentException("ValidFrom")

        if (trustList.validUntil.isBefore(Instant.now()))
            throw IllegalArgumentException("ValidUntil")

        return trustList
    }

    private fun validate(sign1Message: Sign1Message, kid: ByteArray): Boolean {
        repository.loadTrustedCertificates(kid, VerificationResult()).forEach {
            if (sign1Message.validate(it.buildOneKey())) {
                return true
            }
        }
        return false
    }

}