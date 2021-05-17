package ehn.techiop.hcert.kotlin.trust

import COSE.Attribute
import COSE.OneKey
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.common.PkiUtils.toTrustedCertificate
import kotlinx.datetime.Clock
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import java.security.cert.X509Certificate
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

class TrustListEncodeService @OptIn(ExperimentalTime::class) constructor(
    private val signingService: CryptoService,
    private val validity: Duration = 48.toDuration(DurationUnit.HOURS),
    private val clock: Clock = Clock.System,
) {

    fun encode(certificates: Set<X509Certificate>): ByteArray {
        val now = clock.now()
        val trustList = TrustList(
            validFrom = now,
            validUntil = now + validity,
            certificates = certificates.map { it.toTrustedCertificate() }
        )

        return Sign1Message().also {
            it.SetContent(Cbor.encodeToByteArray(trustList))
            signingService.getCborHeaders().forEach { header ->
                it.addAttribute(CBORObject.FromObject(header.first), CBORObject.FromObject(header.second), Attribute.PROTECTED)
            }
            it.addAttribute(CBORObject.FromObject(42), CBORObject.FromObject(1), Attribute.PROTECTED)
            it.sign(signingService.getCborSigningKey().toCoseRepresenation() as OneKey)
        }.EncodeToBytes()
    }

}