package ehn.techiop.hcert.kotlin.trust

import COSE.Attribute
import COSE.OneKey
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.JvmCertificate
import kotlinx.datetime.Clock
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import java.security.MessageDigest
import java.security.cert.X509Certificate
import kotlin.time.Duration


class TrustListV2EncodeService constructor(
    private val signingService: CryptoService,
    private val validity: Duration = Duration.hours(48),
    private val clock: Clock = Clock.System,
) {

    fun encodeContent(certificates: Set<X509Certificate>): ByteArray {
        val trustList = TrustListV2(
            certificates = certificates.map { JvmCertificate(it).toTrustedCertificate() }
        )
        return Cbor.encodeToByteArray(trustList)
    }

    fun encodeSignature(content: ByteArray): ByteArray {
        val validFrom = clock.now()
        val validUntil = validFrom + validity
        val hash = MessageDigest.getInstance("SHA-256").digest(content)
        val cwtClaims = CBORObject.NewMap().also {
            it[CwtHeaderKeys.NOT_BEFORE.value] = CBORObject.FromObject(validFrom.epochSeconds)
            it[CwtHeaderKeys.EXPIRATION.value] = CBORObject.FromObject(validUntil.epochSeconds)
            it[CwtHeaderKeys.SUBJECT.value] = CBORObject.FromObject(hash)
        }

        val versionKey = CBORObject.FromObject(42)
        val versionValue = CBORObject.FromObject(2)
        return Sign1Message().also {
            it.SetContent(cwtClaims.EncodeToBytes())
            signingService.getCborHeaders().forEach { header ->
                it.addAttribute(
                    CBORObject.FromObject(header.first.value),
                    CBORObject.FromObject(header.second),
                    Attribute.PROTECTED
                )
            }
            it.addAttribute(versionKey, versionValue, Attribute.PROTECTED)
            it.sign(signingService.getCborSigningKey().toCoseRepresentation() as OneKey)
        }.EncodeToBytes()
    }
}