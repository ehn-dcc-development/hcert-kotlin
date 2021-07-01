package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.Error.TRUST_SERVICE_ERROR
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import kotlinx.datetime.Clock
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlin.time.Duration

/**
 * Decodes a [SignedData] blob, expected to contain the content and signature of a [TrustListV2]
 *
 * [repository] contains the trust anchor for the parsed file
 * [clock] defines the current time to use for validity checks
 * [clockSkew] defines the error margin when comparing time validity of the parsed file
 */
class TrustListDecodeService(
    repository: CertificateRepository,
    clock: Clock = Clock.System,
    clockSkew: Duration = Duration.seconds(300)
) {

    private val decodeService = SignedDataDecodeService(repository, clock, clockSkew)

    /**
     * See [SignedData] for details about the content
     * If all checks succeed, [trustList.content] is parsed as a [TrustListV2], and the certificates are and returned
     */
    @Throws(VerificationException::class)
    fun decode(trustList: SignedData): List<TrustedCertificate> {
        val parsed = decodeService.decode(trustList, listOf(CoseHeaderKeys.TRUSTLIST_VERSION))
        when (parsed.headers[CoseHeaderKeys.TRUSTLIST_VERSION]) {
            1 -> throw VerificationException(TRUST_SERVICE_ERROR, "Version 1")
            2 -> return Cbor.decodeFromByteArray<TrustListV2>(parsed.content).certificates
            else -> throw VerificationException(TRUST_SERVICE_ERROR, "Version unknown")
        }
    }

}

