package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import kotlinx.datetime.Clock
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import kotlin.jvm.JvmOverloads
import kotlin.time.Duration


/**
 * Encodes a list of certificates as a [SignedData] object
 */
class TrustListV2EncodeService constructor(
    signingService: CryptoService,
    validity: Duration = Duration.hours(48),
    clock: Clock = Clock.System,
) {

    @JvmOverloads
    constructor(signingService: CryptoService, validityHours: Int = 48)
            : this(signingService, Duration.hours(validityHours), Clock.System)

    private val signedDataService = SignedDataEncodeService(signingService, validity, clock)

    /**
     * Content is a CBOR encoded [TrustListV2] object, i.e. a list of entries that contain
     * a KID and a X.509 encoded certificate as bytes
     */
    private fun encodeContent(input: Set<CertificateAdapter>): ByteArray {
        val content = TrustListV2(
            certificates = input.map { it.toTrustedCertificate() }.toTypedArray()
        )
        return Cbor.encodeToByteArray(content)
    }

    /**
     * See [SignedData] for details about returned structure
     */
    fun encode(input: Set<CertificateAdapter>): SignedData {
        val content = encodeContent(input)
        val headers = mapOf(CoseHeaderKeys.TRUSTLIST_VERSION to 2)
        return signedDataService.wrapWithSignature(content, headers)
    }
}
