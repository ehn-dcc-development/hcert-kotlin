package ehn.techiop.hcert.kotlin.valueset

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
import kotlin.jvm.JvmOverloads
import kotlin.time.Duration

/**
 * Decodes a [SignedData] blob, expected to contain the content and signature of a [ValueSetContainer]
 *
 * - [repository] contains the trust anchor for the parsed file
 * - [clock] defines the current time to use for validity checks
 * - [clockSkewSeconds] defines the error margin when comparing time validity of the parsed file in seconds
 */
class ValueSetDecodeService @JvmOverloads constructor(
    repository: CertificateRepository,
    clock: Clock = Clock.System,
    clockSkewSeconds: Int = 300
) {

    private val decodeService = SignedDataDecodeService(repository, clock, clockSkewSeconds)

    /**
     * See [SignedData] for details about the content
     * If all checks succeed, [input.content] is parsed as a [VauleSetContainer]
     */
    @Throws(VerificationException::class)
    fun decode(input: SignedData): Pair<SignedDataParsed, ValueSetContainer> {
        val parsed = decodeService.decode(input, listOf(CoseHeaderKeys.VALUE_SET_VERSION))
        when (val version = parsed.headers[CoseHeaderKeys.VALUE_SET_VERSION]) {
            1 -> return Pair(parsed, Cbor.decodeFromByteArray(parsed.content))
            else -> throw VerificationException(
                TRUST_SERVICE_ERROR, "Version unknown",
                details = mapOf("valueSetVersion" to (version ?: "null").toString())
            )
        }
    }

}

