package ehn.techiop.hcert.kotlin.trust

import COSE.Attribute
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import java.security.cert.X509Certificate
import java.time.Clock
import java.time.Duration

class TrustListV1EncodeService(
    private val signingService: CryptoService,
    private val validity: Duration = Duration.ofHours(48),
    private val clock: Clock = Clock.systemUTC(),
) {

    fun encode(certificates: Set<X509Certificate>): ByteArray {
        val validFrom = clock.instant()
        val validUntil = validFrom + validity
        val trustList = TrustListV1(
            validFrom = validFrom,
            validUntil = validUntil,
            certificates = certificates.map { TrustedCertificateV1.fromCert(it) }
        )

        return Sign1Message().also {
            it.SetContent(Cbor.encodeToByteArray(trustList))
            signingService.getCborHeaders().forEach { header ->
                it.addAttribute(header.first, header.second, Attribute.PROTECTED)
            }
            it.addAttribute(CBORObject.FromObject(42), CBORObject.FromObject(1), Attribute.PROTECTED)
            it.sign(signingService.getCborSigningKey())
        }.EncodeToBytes()
    }

}