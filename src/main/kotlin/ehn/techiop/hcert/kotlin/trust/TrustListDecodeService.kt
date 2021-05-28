package ehn.techiop.hcert.kotlin.trust

import COSE.HeaderKeys
import COSE.MessageTag
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.impl.CwtHeaderKeys
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import java.security.MessageDigest
import java.time.Clock
import java.time.Instant

class TrustListDecodeService(
    private val repository: CertificateRepository,
    private val clock: Clock = Clock.systemUTC(),
) {

    fun decode(signature: ByteArray, content: ByteArray? = null): List<TrustedCertificate> {
        val sign1Message = Sign1Message.DecodeFromBytes(signature, MessageTag.Sign1) as Sign1Message
        val kid = sign1Message.protectedAttributes[HeaderKeys.KID.AsCBOR()].GetByteString()
            ?: throw IllegalArgumentException("kid")

        val validated = validate(sign1Message, kid)
        if (!validated) throw IllegalArgumentException("signature")

        val version = sign1Message.protectedAttributes[CBORObject.FromObject(42)].AsInt32()
        val payload = sign1Message.GetContent()
        if (version == 1) {
            throw IllegalArgumentException("Version")
        } else if (version == 2 && content != null) {
            val cwtMap = CBORObject.DecodeFromBytes(payload)
            val actualHash = MessageDigest.getInstance("SHA256").digest(content)

            val expectedHash = cwtMap[CwtHeaderKeys.SUBJECT.AsCBOR()].GetByteString()
            if (!(expectedHash contentEquals actualHash))
                throw IllegalArgumentException("Hash")

            val validFrom = Instant.ofEpochSecond(cwtMap[CwtHeaderKeys.NOT_BEFORE.AsCBOR()].AsInt64())
            if (validFrom.isAfter(clock.instant().plusSeconds(300)))
                throw IllegalArgumentException("ValidFrom")

            val validUntil = Instant.ofEpochSecond(cwtMap[CwtHeaderKeys.EXPIRATION.AsCBOR()].AsInt64())
            if (validUntil.isBefore(clock.instant().minusSeconds(300)))
                throw IllegalArgumentException("ValidUntil")

            return Cbor.decodeFromByteArray<TrustListV2>(content).certificates
        } else {
            throw IllegalArgumentException("version")
        }
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