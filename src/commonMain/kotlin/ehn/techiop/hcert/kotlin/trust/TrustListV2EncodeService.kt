package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys
import kotlinx.datetime.Clock
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import kotlin.time.Duration


/**
 * Encodes a list of certificates as a content file plus separate signature file
 */
class TrustListV2EncodeService constructor(
    private val signingService: CryptoService,
    private val validity: Duration = Duration.hours(48),
    private val clock: Clock = Clock.System,
) {

    /**
     * Content is a CBOR encoded [TrustListV2] object, i.e. a list of entries that contain
     * a KID and a X.509 encoded certificate as bytes
     */
    fun encodeContent(certificates: Set<CertificateAdapter>): ByteArray {
        val trustList = TrustListV2(
            certificates = certificates.map { it.toTrustedCertificate() }
        )
        return Cbor.encodeToByteArray(trustList)
    }

    /**
     * Signature is a COSE structure with header keys [CoseHeaderKeys.TRUSTLIST_VERSION] and [CoseHeaderKeys.KID],
     * the content of that COSE structure is a CWT map containing
     * - [CwtHeaderKeys.NOT_BEFORE]: seconds since UNIX epoch
     * - [CwtHeaderKeys.EXPIRATION]: seconds since UNIX epoch
     * - [CwtHeaderKeys.SUBJECT]: the SHA-256 hash of the content file
     */
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
