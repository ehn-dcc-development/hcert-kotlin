package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.crypto.Certificate
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys
import kotlinx.datetime.Clock
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import kotlin.time.Duration


class TrustListV2EncodeService constructor(
    private val signingService: CryptoService,
    private val validity: Duration = Duration.hours(48),
    private val clock: Clock = Clock.System,
) {

    fun encodeContent(certificates: Set<Certificate<*>>): ByteArray {
        val trustList = TrustListV2(
            certificates = certificates.map { it.toTrustedCertificate() }
        )
        return Cbor.encodeToByteArray(trustList)
    }

    fun encodeSignature(content: ByteArray): ByteArray {
        val validFrom = clock.now()
        val validUntil = validFrom + validity
        val hash = Hash(content).calc()
        val cwt = CwtCreationAdapter()
        cwt.add(CwtHeaderKeys.NOT_BEFORE.intVal, validFrom.epochSeconds)
        cwt.add(CwtHeaderKeys.EXPIRATION.intVal, validUntil.epochSeconds)
        cwt.add(CwtHeaderKeys.SUBJECT.intVal, hash)
        val cose = CoseCreationAdapter(cwt.encode())
        signingService.getCborHeaders().forEach {
            cose.addProtectedAttribute(it.first, it.second)
        }
        cose.addProtectedAttribute(CoseHeaderKeys.TRUSTLIST_VERSION, 2)
        cose.sign(signingService.getCborSigningKey())
        return cose.encode()
    }
}
