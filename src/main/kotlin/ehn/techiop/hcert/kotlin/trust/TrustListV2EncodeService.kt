package ehn.techiop.hcert.kotlin.trust

import COSE.Attribute
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.impl.CwtHeaderKeys
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import java.security.MessageDigest
import java.security.cert.X509Certificate
import java.time.Clock
import java.time.Duration

class TrustListV2EncodeService(
    private val signingService: CryptoService,
    private val validity: Duration = Duration.ofHours(48),
    private val clock: Clock = Clock.systemUTC(),
) {

    fun encodeContent(certificates: Set<X509Certificate>): ByteArray {
        val trustList = TrustListV2(
            certificates = certificates.map { TrustedCertificateV2.fromCert(it) }
        )
        return Cbor.encodeToByteArray(trustList)
    }

    fun encodeSignature(content: ByteArray): ByteArray {
        val validFrom = clock.instant()
        val validUntil = validFrom + validity
        val hash = MessageDigest.getInstance("SHA-256").digest(content)
        val cwtClaims = CBORObject.NewMap().also {
            it[CwtHeaderKeys.NOT_BEFORE.AsCBOR()] = CBORObject.FromObject(validFrom.epochSecond)
            it[CwtHeaderKeys.EXPIRATION.AsCBOR()] = CBORObject.FromObject(validUntil.epochSecond)
            it[CwtHeaderKeys.SUBJECT.AsCBOR()] = CBORObject.FromObject(hash)
        }

        val versionKey = CBORObject.FromObject(42)
        val versionValue = CBORObject.FromObject(2)
        return Sign1Message().also {
            it.SetContent(cwtClaims.EncodeToBytes())
            signingService.getCborHeaders().forEach { header ->
                it.addAttribute(header.first, header.second, Attribute.PROTECTED)
            }
            it.addAttribute(versionKey, versionValue, Attribute.PROTECTED)
            it.sign(signingService.getCborSigningKey())
        }.EncodeToBytes()
    }

}