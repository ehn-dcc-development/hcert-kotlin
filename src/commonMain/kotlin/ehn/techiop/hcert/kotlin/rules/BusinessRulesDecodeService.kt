package ehn.techiop.hcert.kotlin.rules

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.Error.TRUST_SERVICE_ERROR
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.trust.SignedData
import ehn.techiop.hcert.kotlin.trust.SignedDataDecodeService
import ehn.techiop.hcert.kotlin.trust.SignedDataParsed
import kotlinx.datetime.Clock
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlin.time.Duration

/**
 * Decodes a [SignedData] blob, expected to contain the content and signature of a [BusinessRulesContainer]
 *
 * - [repository] contains the trust anchor for the parsed file
 * - [clock] defines the current time to use for validity checks
 * - [clockSkew] defines the error margin when comparing time validity of the parsed file
 */
class BusinessRulesDecodeService(
    repository: CertificateRepository,
    clock: Clock = Clock.System,
    clockSkew: Duration = Duration.seconds(300)
) {

    private val decodeService = SignedDataDecodeService(repository, clock, clockSkew)

    /**
     * See [SignedData] for details about the content
     * If all checks succeed, [input.content] is parsed as a [BusinessRulesContainer]
     */
    @Throws(VerificationException::class)
    fun decode(input: SignedData): Pair<SignedDataParsed, BusinessRulesContainer> {
        val parsed = decodeService.decode(input, listOf(CoseHeaderKeys.BUSINESS_RULES_VERSION))
        when (parsed.headers[CoseHeaderKeys.BUSINESS_RULES_VERSION]) {
            1 -> return Pair(parsed, Cbor.decodeFromByteArray(parsed.content))
            else -> throw VerificationException(TRUST_SERVICE_ERROR, "Version unknown")
        }
    }

}

