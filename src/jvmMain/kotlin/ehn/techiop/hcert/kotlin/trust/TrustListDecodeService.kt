package ehn.techiop.hcert.kotlin.trust

import COSE.HeaderKeys
import COSE.MessageTag
import COSE.OneKey
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import kotlinx.datetime.Clock
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration


actual class TrustListDecodeService actual constructor(
    private val repository: CertificateRepository,
    private val clock: Clock
) {

    @OptIn(ExperimentalTime::class)
    actual fun decode(input: ByteArray): TrustList {
        val sign1Message = Sign1Message.DecodeFromBytes(input, MessageTag.Sign1) as Sign1Message
        val kid = sign1Message.protectedAttributes[HeaderKeys.KID.AsCBOR()].GetByteString()
            ?: throw IllegalArgumentException("kid")

        val validated = validate(sign1Message, kid)
        if (!validated) throw IllegalArgumentException("signature")

        val version = sign1Message.protectedAttributes[CBORObject.FromObject(42)].AsInt32()
        if (version != 1) throw IllegalArgumentException("version")

        val payload = sign1Message.GetContent()
        val trustList = Cbor.decodeFromByteArray<TrustList>(payload)

        if (trustList.validFrom > (clock.now().plus(300.toDuration(DurationUnit.SECONDS))))
            throw IllegalArgumentException("ValidFrom")

        if (trustList.validUntil < (clock.now().minus(300.toDuration(DurationUnit.SECONDS))))
            throw IllegalArgumentException("ValidUntil")

        return trustList
    }

    private fun validate(sign1Message: Sign1Message, kid: ByteArray): Boolean {
        repository.loadTrustedCertificates(kid, VerificationResult()).forEach {
            if (sign1Message.validate(it.cosePublicKey.toCoseRepresentation() as OneKey)) {
                return true
            }
        }
        return false
    }

}