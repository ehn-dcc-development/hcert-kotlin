package ehn.techiop.hcert.kotlin.trust

import COSE.Attribute
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import java.security.cert.X509Certificate
import java.time.Instant
import java.time.temporal.ChronoUnit

class TrustListEncodeService(private val signingService: CryptoService, private val validityHours: Long = 48) {

    fun encode(certificates: Set<X509Certificate>): ByteArray {
        val now = Instant.now()
        val trustList = TrustList(
            validFrom = now,
            validUntil = now.plus(validityHours, ChronoUnit.HOURS),
            certificates = certificates.map { TrustedCertificate.fromCert(it) }
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