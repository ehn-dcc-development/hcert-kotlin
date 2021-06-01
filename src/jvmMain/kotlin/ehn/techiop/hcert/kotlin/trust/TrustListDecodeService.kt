package ehn.techiop.hcert.kotlin.trust

import COSE.HeaderKeys
import COSE.MessageTag
import COSE.OneKey
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant.Companion.fromEpochSeconds
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import java.security.MessageDigest
import kotlin.time.Duration


actual class TrustListDecodeService actual constructor(
    private val repository: CertificateRepository,
    private val clock: Clock
) {

    actual fun decode(input: ByteArray, optionalContent: ByteArray?): List<TrustedCertificate> {
        val sign1Message = Sign1Message.DecodeFromBytes(input, MessageTag.Sign1) as Sign1Message
        val kid = sign1Message.protectedAttributes[HeaderKeys.KID.AsCBOR()].GetByteString()
            ?: throw IllegalArgumentException("kid")

        val validated = validate(sign1Message, kid)
        if (!validated) throw IllegalArgumentException("signature")

        val version = sign1Message.protectedAttributes[CBORObject.FromObject(42)].AsInt32()
        val payload = sign1Message.GetContent()
        if (version == 1) {
            throw IllegalArgumentException("V1")
        } else if (version == 2 && optionalContent != null) {
            val cwtMap = CBORObject.DecodeFromBytes(payload)
            val actualHash = MessageDigest.getInstance("SHA-256").digest(optionalContent)

            val expectedHash = cwtMap[CwtHeaderKeys.SUBJECT.value].GetByteString()
            if (!(expectedHash contentEquals actualHash))
                throw IllegalArgumentException("Hash")

            val validFrom = fromEpochSeconds(cwtMap[CwtHeaderKeys.NOT_BEFORE.value].AsInt64())
            if (validFrom > clock.now().plus(Duration.seconds(300)))
                throw IllegalArgumentException("ValidFrom")

            val validUntil = fromEpochSeconds(cwtMap[CwtHeaderKeys.EXPIRATION.value].AsInt64())
            if (validUntil < clock.now().minus(Duration.seconds(300)))
                throw IllegalArgumentException("ValidUntil")

            return Cbor.decodeFromByteArray<TrustListV2>(optionalContent).certificates
        } else {
            throw IllegalArgumentException("version")
        }
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