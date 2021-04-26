package ehn.techiop.hcert.kotlin.chain

import COSE.Attribute
import COSE.MessageTag
import COSE.Sign1Message
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import java.security.cert.X509Certificate
import java.time.Instant
import java.time.temporal.ChronoUnit

class TrustListService(private val signingService: CryptoService) {

    fun encode(certificates: Set<X509Certificate>): ByteArray {
        val now = Instant.now()
        val trustList = TrustList(
            validFrom = now,
            validUntil = now.plus(7, ChronoUnit.DAYS),
            certificates = certificates.map { TrustedCertificate.fromCert(PkiUtils.calcKid(it), it) }
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

    fun decode(input: ByteArray): TrustList {
        val payload = (Sign1Message.DecodeFromBytes(input, MessageTag.Sign1) as Sign1Message).also {
            DefaultCoseService.getKid(it)?.let { kid ->
                val key = signingService.getCborVerificationKey(kid)
                if (!it.validate(key))
                    throw IllegalArgumentException("Signature not verified")
            } ?: throw IllegalArgumentException("KID not found in headers")
        }.GetContent()
        val trustList = Cbor { }.decodeFromByteArray<TrustList>(payload)
        if (trustList.validFrom.isAfter(Instant.now())) throw IllegalArgumentException("ValidFrom after now")
        if (trustList.validUntil.isBefore(Instant.now())) throw IllegalArgumentException("ValidUntil before now")
        return trustList
    }

}