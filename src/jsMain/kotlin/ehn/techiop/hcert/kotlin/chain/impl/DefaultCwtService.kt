package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CwtService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.mapToJson
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.crypto.Buffer
import ehn.techiop.hcert.kotlin.crypto.Cbor
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.khronos.webgl.Uint8Array
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Encodes/decodes input as a CWT structure, ready to sign with COSE
 */
actual open class DefaultCwtService @OptIn(ExperimentalTime::class) actual constructor(
    private val countryCode: String,
    private val validity: Duration,
    private val clock: Clock,
) : CwtService {

    override fun encode(input: ByteArray): ByteArray {
        val issueTime = clock.now()
        val expirationTime = issueTime + validity
        val map = mapOf<Any, Any>(
            CwtHeaderKeys.ISSUER to countryCode,
            CwtHeaderKeys.ISSUED_AT to issueTime.epochSeconds,
            CwtHeaderKeys.EXPIRATION to expirationTime.epochSeconds,
            CwtHeaderKeys.HCERT to mapOf(
                1 to input
            )
        )
        val export = map.mapToJson()
        val buffer = Cbor.encode(export)
        return Buffer.toByteArray(buffer)
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.cwtDecoded = false
        try {
            val cbor = Cbor.decode(input)
            (cbor["value"].get(CwtHeaderKeys.ISSUER) as Any?)?.let {
                verificationResult.issuer = it.toString()
            }
            (cbor["value"].get(CwtHeaderKeys.ISSUED_AT) as Any?)?.let {
                verificationResult.issuedAt = Instant.fromEpochSeconds(it as Long)
            }
            (cbor["value"].get(CwtHeaderKeys.EXPIRATION) as Any?)?.let {
                verificationResult.expirationTime = Instant.fromEpochSeconds(it as Long)
            }

            val hcert = cbor["value"].get(CwtHeaderKeys.HCERT)
            if (hcert !== undefined) {
                val eudgcV1 = hcert.get(1)
                if (eudgcV1 !== undefined) {
                    verificationResult.cwtDecoded = true
                    return (eudgcV1 as Uint8Array).toByteArray()
                }
            }
            return input
        } catch (e: Throwable) {
            return input
        }
    }

}
