package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CwtService
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys
import ehn.techiop.hcert.kotlin.data.CborObject
import ehn.techiop.hcert.kotlin.trust.CwtAdapter
import ehn.techiop.hcert.kotlin.trust.CwtCreationAdapter
import ehn.techiop.hcert.kotlin.trust.CwtHelper
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
        cwtAdapter.add(CwtHeaderKeys.ISSUER.intVal, countryCode)
        cwtAdapter.add(CwtHeaderKeys.ISSUED_AT.intVal, issueTime.epochSeconds)
        cwtAdapter.add(CwtHeaderKeys.EXPIRATION.intVal, expirationTime.epochSeconds)
        cwtAdapter.addDgc(CwtHeaderKeys.HCERT.intVal, CwtHeaderKeys.EUDGC_IN_HCERT.intVal, input)
        return cwtAdapter.encode()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): CborObject {
        try {
            val map = CwtHelper.fromCbor(input)

            map.getString(CwtHeaderKeys.ISSUER.intVal)?.let {
                verificationResult.issuer = it
            }

            map.getNumber(CwtHeaderKeys.ISSUED_AT.intVal)?.let {
                val issuedAt = Instant.fromEpochSeconds(it.toLong())
                verificationResult.issuedAt = issuedAt
                verificationResult.certificateValidFrom?.let { certValidFrom ->
                    if (issuedAt < certValidFrom)
                        throw VerificationException(Error.CWT_EXPIRED, "issuedAt<certValidFrom")
                }
                if (issuedAt > clock.now())
                    throw VerificationException(Error.CWT_EXPIRED, "issuedAt>clock.now()")
            }

            map.getNumber(CwtHeaderKeys.EXPIRATION.intVal)?.let {
                val expirationTime = Instant.fromEpochSeconds(it.toLong())
                verificationResult.expirationTime = expirationTime
                verificationResult.certificateValidUntil?.let { certValidUntil ->
                    if (expirationTime > certValidUntil)
                        throw VerificationException(Error.CWT_EXPIRED, "expirationTime>certValidUntil")
                }
                if (expirationTime < clock.now())
                    throw VerificationException(Error.CWT_EXPIRED, "expirationTime<clock.now()")
            }

            map.getMap(CwtHeaderKeys.HCERT.intVal)?.let { hcert ->
                hcert.getMap(CwtHeaderKeys.EUDGC_IN_HCERT.intVal)?.let { eudgcV1 ->
                    return eudgcV1.toCborObject()
                }
            }

            throw VerificationException(Error.CBOR_DESERIALIZATION_FAILED, "CWT contains no HCERT or EUDGC")
        } catch (e: VerificationException) {
            throw e
        } catch (e: Throwable) {
            throw VerificationException(Error.CBOR_DESERIALIZATION_FAILED, e.message, e)
        }
    }

}
