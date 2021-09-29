package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.Error.TRUST_SERVICE_ERROR
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import kotlinx.datetime.Clock
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlin.jvm.JvmOverloads
import kotlin.time.Duration

/**
 * Decodes a [SignedData] blob, expected to contain the content and signature of a [TrustListV2]
 *
 * - [repository] contains the trust anchor for the parsed file
 * - [clock] defines the current time to use for validity checks
 * - [clockSkewSeconds] defines the error margin when comparing time validity of the parsed file in seconds
 */
class TrustListDecodeService @JvmOverloads constructor(
    repository: CertificateRepository,
    clock: Clock = Clock.System,
    clockSkewSeconds: Int = 300
) {

    private val decodeService = SignedDataDecodeService(repository, clock, clockSkewSeconds)

    /**
     * See [SignedData] for details about the content
     * If all checks succeed, [input.content] is parsed as a [TrustListV2], and the certificates are and returned
     */
    @Throws(VerificationException::class)
    fun decode(input: SignedData): Pair<SignedDataParsed, TrustListV2> {
        val parsed = decodeService.decode(input, listOf(CoseHeaderKeys.TRUSTLIST_VERSION))
        when (val version = parsed.headers[CoseHeaderKeys.TRUSTLIST_VERSION]) {
            1 -> throw VerificationException(
                TRUST_SERVICE_ERROR, "Version 1",
                details = mapOf("trustListVersion" to version.toString())
            )
            2 -> return Pair(parsed, Cbor.decodeFromByteArray(parsed.content))
            else -> throw VerificationException(
                TRUST_SERVICE_ERROR, "Version unknown",
                details = mapOf("trustListVersion" to (version ?: "null").toString())
            )
        }
    }

}

