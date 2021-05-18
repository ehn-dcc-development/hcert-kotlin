package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CwtService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.mapToJson
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toHexString
import ehn.techiop.hcert.kotlin.crypto.Buffer
import ehn.techiop.hcert.kotlin.crypto.Cbor
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.khronos.webgl.Uint8Array
import kotlin.math.exp
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
            CwtHeaderKeys.ISSUER.value to countryCode,
            CwtHeaderKeys.ISSUED_AT.value to issueTime.epochSeconds,
            CwtHeaderKeys.EXPIRATION.value to expirationTime.epochSeconds,
            CwtHeaderKeys.HCERT.value to mapOf(
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
            val cbor = Cbor.decode(input)[0]
            val issuer = cbor.get(CwtHeaderKeys.ISSUER.value)
            if (issuer !== undefined) {
                verificationResult.issuer = issuer.toString()
            }
            val issuedAt = cbor.get(CwtHeaderKeys.ISSUED_AT.value)
            if (issuedAt !== undefined) {
                verificationResult.issuedAt = Instant.fromEpochSeconds((issuedAt as Number).toLong())
            }
            val expiration = cbor.get(CwtHeaderKeys.EXPIRATION.value)
            if (expiration !== undefined) {
                verificationResult.expirationTime = Instant.fromEpochSeconds((expiration as Number).toLong())
            }

            val hcert = cbor.get(CwtHeaderKeys.HCERT.value)
            if (hcert !== undefined) {
                val eudgcV1 = hcert.get(1)
                if (eudgcV1 !== undefined) {
                    verificationResult.cwtDecoded = true
                    return Cbor.encode(eudgcV1)
                }
            }
            return input
        } catch (e: Throwable) {
            e.printStackTrace()
            return input
        }
    }

}
