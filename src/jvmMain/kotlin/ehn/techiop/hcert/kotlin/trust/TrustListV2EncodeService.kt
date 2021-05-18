package ehn.techiop.hcert.kotlin.trust

import COSE.Attribute
import COSE.OneKey
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils.toTrustedCertificate
import ehn.techiop.hcert.kotlin.chain.impl.CwtHeaderKeys
import kotlinx.datetime.Clock
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import java.security.MessageDigest
import java.security.cert.X509Certificate
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


class TrustListV2EncodeService constructor(
    private val signingService: CryptoService,
    private val validity: Duration = Duration.hours(48),
    private val clock: Clock = Clock.System,
) {

    fun encode(certificates: Set<X509Certificate>): ByteArray {
        val trustList = TrustListV2(
            certificates = certificates.map { it.toTrustedCertificate() }
        )

        return Sign1Message().also {
            it.SetContent(Cbor.encodeToByteArray(trustList))
            signingService.getCborHeaders().forEach { header ->
                it.addAttribute(
                    CBORObject.FromObject(header.first.value),
                    CBORObject.FromObject(header.second),
                    Attribute.PROTECTED
                )
            }
            it.addAttribute(CBORObject.FromObject(42), CBORObject.FromObject(1), Attribute.PROTECTED)
            it.sign(signingService.getCborSigningKey().toCoseRepresentation() as OneKey)
        }.EncodeToBytes()
    }

    fun encodeContent(certificates: Set<X509Certificate>): ByteArray {
        val trustList = TrustListV2(
            certificates = certificates.map { it.toTrustedCertificate() }
        )
        return Cbor.encodeToByteArray(trustList)
    }

    fun encodeSignature(content: ByteArray): ByteArray {
        val validFrom = clock.now()
        val validUntil = validFrom + validity
        val hash = MessageDigest.getInstance("SHA-256").digest(content)
        val cwtClaims = CBORObject.NewMap().also {
            it[CwtHeaderKeys.NOT_BEFORE.AsCBOR()] = CBORObject.FromObject(validFrom.epochSeconds)
            it[CwtHeaderKeys.EXPIRATION.AsCBOR()] = CBORObject.FromObject(validUntil.epochSeconds)
            it[CwtHeaderKeys.SUBJECT.AsCBOR()] = CBORObject.FromObject(hash)
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