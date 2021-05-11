package ehn.techiop.hcert.kotlin.trust

import COSE.Attribute
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.impl.CwtHeaderKeys
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import java.security.cert.X509Certificate
import java.time.Clock
import java.time.Duration

class TrustListV2EncodeService(
    private val signingService: CryptoService,
    private val validity: Duration = Duration.ofHours(48),
    private val clock: Clock = Clock.systemUTC(),
) {

    fun encode(certificates: Set<X509Certificate>): ByteArray {
        val validFrom = clock.instant()
        val validUntil = validFrom + validity
        // TODO CWT Headers not in COSE headers!
        val trustList = TrustListV2(
            certificates = certificates.map { TrustedCertificateV2.fromCert(it) }
        )

        return Sign1Message().also {
            it.SetContent(Cbor.encodeToByteArray(trustList))
            signingService.getCborHeaders().forEach { header ->
                it.addAttribute(header.first, header.second, Attribute.PROTECTED)
            }
            it.addAttribute(CBORObject.FromObject(42), CBORObject.FromObject(2), Attribute.PROTECTED)
            it.addAttribute(
                CwtHeaderKeys.ISSUED_AT.AsCBOR(),
                CBORObject.FromObject(validFrom.epochSecond),
                Attribute.PROTECTED
            )
            it.addAttribute(
                CwtHeaderKeys.NOT_BEFORE.AsCBOR(),
                CBORObject.FromObject(validUntil.epochSecond),
                Attribute.PROTECTED
            )
            it.sign(signingService.getCborSigningKey())
        }.EncodeToBytes()
    }

}