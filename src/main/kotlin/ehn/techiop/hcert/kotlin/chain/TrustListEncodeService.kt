package ehn.techiop.hcert.kotlin.chain

import COSE.Attribute
import COSE.Sign1Message
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import java.security.cert.X509Certificate
import java.time.Instant
import java.time.temporal.ChronoUnit

class TrustListEncodeService(private val signingService: CryptoService) {

    fun encode(certificates: Set<X509Certificate>): ByteArray {
        val now = Instant.now()
        val trustList = TrustList(
            validFrom = now,
            validUntil = now.plus(7, ChronoUnit.DAYS),
            certificates = certificates.map { TrustedCertificate.fromCert(it) }
        )

        return Sign1Message().also {
            it.SetContent(Cbor { }.encodeToByteArray(trustList))
            // TODO Version-Info in den Header, falls sich die Struktur doch noch ändert!
            signingService.getCborHeaders().forEach { header ->
                it.addAttribute(header.first, header.second, Attribute.PROTECTED)
            }
            it.sign(signingService.getCborSigningKey())
        }.EncodeToBytes()
    }

}