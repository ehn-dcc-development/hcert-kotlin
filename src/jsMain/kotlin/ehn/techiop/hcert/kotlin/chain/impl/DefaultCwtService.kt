package ehn.techiop.hcert.kotlin.chain.impl

import Buffer
import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

/**
 * Encodes/decodes input as a CWT structure, ready to sign with COSE
 */
actual open class DefaultCwtService actual constructor(
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
        val buffer = Cbor.Encoder.encode(export)
        return buffer.toByteArray()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.cwtDecoded = false
        return jsTry {
            // TODO: Can we get rid of dynamic here?
            val cwtMap = Cbor.Decoder.decodeAllSync(Buffer.from(input.toUint8Array()))[0].asDynamic()
            val issuer = cwtMap.get(CwtHeaderKeys.ISSUER.value)
            if (issuer !== undefined) {
                verificationResult.issuer = issuer.toString()
            }

            val issuedAt = cwtMap.get(CwtHeaderKeys.ISSUED_AT.value)
            if (issuedAt !== undefined) {
                verificationResult.issuedAt = Instant.fromEpochSeconds((issuedAt as Number).toLong())
            }

            val expiration = cwtMap.get(CwtHeaderKeys.EXPIRATION.value)
            if (expiration !== undefined) {
                verificationResult.expirationTime = Instant.fromEpochSeconds((expiration as Number).toLong())
            }

            val hcert = cwtMap.get(CwtHeaderKeys.HCERT.value)
            if (hcert !== undefined) {

                val eudgcV1 = (hcert).get(1)
                if (eudgcV1 !== undefined) {
                    verificationResult.cwtDecoded = true
                    return@jsTry Cbor.Encoder.encode(eudgcV1).toByteArray()
                }
            }
            throw Throwable("could not decode CWT. hcert:$hcert, expiration: $expiration, issuedAt: $issuedAt, issuer: $issuer")
        }.catch{
             throw it
        }
    }

}
