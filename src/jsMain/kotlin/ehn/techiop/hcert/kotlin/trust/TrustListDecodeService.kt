package ehn.techiop.hcert.kotlin.trust

import Buffer
import Hash
import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array
import ehn.techiop.hcert.kotlin.crypto.Cose
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys
import kotlinx.datetime.Clock
import kotlinx.serialization.cbor.Cbor as KCbor
import kotlinx.serialization.decodeFromByteArray
import org.khronos.webgl.Uint8Array
import kotlin.time.Duration

actual class TrustListDecodeService actual constructor(
    private val repository: CertificateRepository,
    private val clock: Clock
) {

    actual fun decode(input: ByteArray, optionalContent: ByteArray?): List<TrustedCertificate> {
        val cborJson = Cbor.Decoder.decodeAllSync(Buffer(input.toUint8Array()))
        val cose = cborJson[0] as Cbor.Tagged
        val coseValue = cose.value as Array<Buffer>
        val protectedHeader = coseValue[0]
        val unprotectedHeader = coseValue[1].asDynamic()
        val content = coseValue[2]
        val signature = coseValue[3]

        val protectedHeaderCbor = Cbor.Decoder.decodeAllSync(protectedHeader)[0].asDynamic()
        val kid = protectedHeaderCbor?.get(4) as Uint8Array? ?: throw IllegalArgumentException("kid")

        val validated = validate(input, kid.toByteArray())
        if (!validated) throw IllegalArgumentException("signature")

        val version = protectedHeaderCbor?.get(42) as Int? ?: throw IllegalArgumentException("version")
        if (version == 1) {
            throw IllegalArgumentException("V1")
        } else if (version == 2 && optionalContent != null) {
            val cwtMap = Cbor.Decoder.decodeAllSync(Buffer.from(input.toUint8Array()))[0].asDynamic()
            val actualHash = calcHash(optionalContent)
            val expectedHash = (cwtMap[CwtHeaderKeys.SUBJECT] as Buffer).toByteArray()
            if (!(expectedHash.contentEquals(actualHash))) {
                throw IllegalArgumentException("Hash")
            }

            val validFrom =
                kotlinx.datetime.Instant.fromEpochSeconds(cwtMap[CwtHeaderKeys.NOT_BEFORE] as Long)
            if (validFrom > clock.now().plus(Duration.seconds(300)))
                throw IllegalArgumentException("ValidFrom")

            val validUntil =
                kotlinx.datetime.Instant.fromEpochSeconds(cwtMap[CwtHeaderKeys.EXPIRATION] as Long)
            if (validUntil < clock.now().minus(Duration.seconds(300)))
                throw IllegalArgumentException("ValidUntil")

            return KCbor.decodeFromByteArray<TrustListV2>(optionalContent).certificates
        } else {
            throw IllegalArgumentException("version")
        }
    }

    private fun calcHash(input: ByteArray): ByteArray {
        val hash = Hash()
        hash.update(input.toUint8Array())
        return hash.digest().toByteArray()
    }

    private fun validate(input: ByteArray, kid: ByteArray): Boolean {
        repository.loadTrustedCertificates(kid, VerificationResult()).forEach {
            try {
                val result = Cose.verify(input, it.cosePublicKey)
                if (result !== undefined) return true
            } catch (ignored: dynamic) {
            }
        }
        return false
    }


}