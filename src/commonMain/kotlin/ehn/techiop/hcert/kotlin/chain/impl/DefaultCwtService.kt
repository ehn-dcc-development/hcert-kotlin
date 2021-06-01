package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CwtService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys
import ehn.techiop.hcert.kotlin.trust.CwtAdapter
import ehn.techiop.hcert.kotlin.trust.CwtCreationAdapter
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

/**
 * Encodes/decodes input as a CWT structure, ready to sign with COSE
 */
open class DefaultCwtService constructor(
    private val countryCode: String = "AT",
    private val validity: Duration = Duration.hours(48),
    private val clock: Clock = Clock.System,
) : CwtService {

    override fun encode(input: ByteArray): ByteArray {
        val issueTime = clock.now()
        val expirationTime = issueTime + validity
        val cwtAdapter = CwtCreationAdapter()
        cwtAdapter.add(CwtHeaderKeys.ISSUER.value, countryCode)
        cwtAdapter.add(CwtHeaderKeys.ISSUED_AT.value, issueTime.epochSeconds)
        cwtAdapter.add(CwtHeaderKeys.EXPIRATION.value, expirationTime.epochSeconds)
        cwtAdapter.addDgc(CwtHeaderKeys.HCERT.value, CwtHeaderKeys.EUDGC_IN_HCERT.value, input)
        return cwtAdapter.encode()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.cwtDecoded = false
        val map = CwtAdapter(input)

        map.getString(CwtHeaderKeys.ISSUER.value)?.let {
            verificationResult.issuer = it
        }
        map.getNumber(CwtHeaderKeys.ISSUED_AT.value)?.let {
            verificationResult.issuedAt = Instant.fromEpochSeconds(it.toLong())
        }
        map.getNumber(CwtHeaderKeys.EXPIRATION.value)?.let {
            verificationResult.expirationTime = Instant.fromEpochSeconds(it.toLong())
        }

        map.getMap(CwtHeaderKeys.HCERT.value)?.let { hcert ->
            hcert.getMap(CwtHeaderKeys.EUDGC_IN_HCERT.value)?.let { eudgcV1 ->
                return eudgcV1.encoded().also {
                    verificationResult.cwtDecoded = true
                }
            }
        }
        throw Throwable("Decode CWT")
    }

}