package ehn.techiop.hcert.kotlin.chain.debug

import ehn.techiop.hcert.kotlin.chain.*
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
open class DebugCwtService constructor(
    private val countryCode: String = "AT",
    private val validity: Duration = Duration.hours(48),
    private val clock: Clock = Clock.System,
) : CwtService {

    constructor(countryCode: String, validityHours: Int)
            : this(countryCode, Duration.hours(validityHours), Clock.System)

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
            val now = clock.now()
            val map = CwtHelper.fromCbor(input)
            // issuer is truly optional
            map.getString(CwtHeaderKeys.ISSUER.intVal)?.let {
                verificationResult.issuer = it
            }

            val issuedAtSeconds = map.getNumber(CwtHeaderKeys.ISSUED_AT.intVal)
                ?: throw NonFatalVerificationException(map.getDgc(), Error.CWT_EXPIRED, details = mapOf("issuedAt" to "null"))
            val issuedAt = Instant.fromEpochSeconds(issuedAtSeconds.toLong())
            verificationResult.issuedAt = issuedAt
            val certValidFrom = verificationResult.certificateValidFrom
                ?: throw NonFatalVerificationException(map.getDgc(),
            Error.PUBLIC_KEY_NOT_YET_VALID,
                    details = mapOf("certValidFrom" to "null")
                )

            if (issuedAt > now)
                throw NonFatalVerificationException(map.getDgc(),
                        Error.CWT_NOT_YET_VALID, details = mapOf(
                        "issuedAt" to issuedAt.toString(),
                        "currentTime" to now.toString()
                    )
                )

            if (certValidFrom > now)
                throw NonFatalVerificationException(map.getDgc(),
                        Error.PUBLIC_KEY_NOT_YET_VALID, details = mapOf(
                        "certValidFrom" to certValidFrom.toString(),
                        "currentTime" to now.toString()
                    )
                )

            val expirationSeconds = map.getNumber(CwtHeaderKeys.EXPIRATION.intVal)
                ?: throw NonFatalVerificationException(map.getDgc(), Error.CWT_EXPIRED, details = mapOf("expirationTime" to "null"))
            val expirationTime = Instant.fromEpochSeconds(expirationSeconds.toLong())
            verificationResult.expirationTime = expirationTime
            val certValidUntil = verificationResult.certificateValidUntil
                ?: throw NonFatalVerificationException(map.getDgc(), Error.PUBLIC_KEY_EXPIRED, details = mapOf("certValidUntil" to "null"))

            if (certValidUntil < now)
                throw NonFatalVerificationException(map.getDgc(),
                        Error.PUBLIC_KEY_EXPIRED, details = mapOf(
                        "certValidUntil" to certValidUntil.toString(),
                        "currentTime" to now.toString()
                    )
                )

            if (expirationTime < now)
                throw NonFatalVerificationException(map.getDgc(),
                        Error.CWT_EXPIRED, details = mapOf(
                        "expirationTime" to expirationTime.toString(),
                        "currentTime" to now.toString()
                    )
                )

           return map.getDgc()
        } catch (e: VerificationException) {
            throw e
        } catch (e: Throwable) {
            throw VerificationException(Error.CBOR_DESERIALIZATION_FAILED, e.message, e)
        }
    }

    private fun CwtAdapter.getDgc(): CborObject {
        val hcert: CwtAdapter =getMap(CwtHeaderKeys.HCERT.intVal)
                ?: throw VerificationException(Error.CBOR_DESERIALIZATION_FAILED, "CWT contains no HCERT")

        val dgc = hcert.getMap(CwtHeaderKeys.EUDGC_IN_HCERT.intVal)
                ?: throw VerificationException(Error.CBOR_DESERIALIZATION_FAILED, "CWT contains no EUDGC")

        return dgc.toCborObject()
    }

}
