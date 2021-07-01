package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import kotlinx.datetime.Clock
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import kotlin.time.Duration


/**
 * Encodes a list of certificates as a content file plus separate signature file
 */
class TrustListV2EncodeService constructor(
    signingService: CryptoService,
    validity: Duration = Duration.hours(48),
    clock: Clock = Clock.System,
) {

    private val contentAndSignatureService = SignedDataEncodeService(signingService, validity, clock)

    /**
     * Content is a CBOR encoded [TrustListV2] object, i.e. a list of entries that contain
     * a KID and a X.509 encoded certificate as bytes
     */
    private fun encodeContent(certificates: Set<CertificateAdapter>): ByteArray {
        val trustList = TrustListV2(
            certificates = certificates.map { it.toTrustedCertificate() }
        )
        return Cbor.encodeToByteArray(trustList)
    }

    /**
     * See [SignedData] for details about returned structure
     */
    fun encode(certificates: Set<CertificateAdapter>): SignedData {
        val content = encodeContent(certificates)
        return contentAndSignatureService.wrapWithSignature(content, mapOf(CoseHeaderKeys.TRUSTLIST_VERSION to 2))
    }
}
