package ehn.techiop.hcert.kotlin.chain.impl

import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CwtService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
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

    private val keyEuDgcV1 = CBORObject.FromObject(1)

    override fun encode(input: ByteArray): ByteArray {
        val issueTime = clock.now()
        val expirationTime = issueTime + validity
        return CBORObject.NewMap().also {
            it[CwtHeaderKeys.ISSUER.AsCBOR()] = CBORObject.FromObject(countryCode)
            it[CwtHeaderKeys.ISSUED_AT.AsCBOR()] = CBORObject.FromObject(issueTime.epochSeconds)
            it[CwtHeaderKeys.EXPIRATION.AsCBOR()] = CBORObject.FromObject(expirationTime.epochSeconds)
            it[CwtHeaderKeys.HCERT.AsCBOR()] = CBORObject.NewMap().also { hcert ->
                try {
                    hcert[keyEuDgcV1] = CBORObject.DecodeFromBytes(input)
                } catch (e: Throwable) {
                    hcert[keyEuDgcV1] = CBORObject.FromObject(input)
                }
            }
        }.EncodeToBytes()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.cwtDecoded = false
        try {
            val map = CBORObject.DecodeFromBytes(input)

            map[CwtHeaderKeys.ISSUER.AsCBOR()]?.let {
                verificationResult.issuer = it.AsString()
            }
            map[CwtHeaderKeys.ISSUED_AT.AsCBOR()]?.let {
                verificationResult.issuedAt = Instant.fromEpochSeconds(it.AsInt64())
            }
            map[CwtHeaderKeys.EXPIRATION.AsCBOR()]?.let {
                verificationResult.expirationTime = Instant.fromEpochSeconds(it.AsInt64())
            }

            map[CwtHeaderKeys.HCERT.AsCBOR()]?.let { hcert -> // SPEC
                hcert[keyEuDgcV1]?.let { eudgcV1 ->
                    return getContents(eudgcV1).also {
                        verificationResult.cwtDecoded = true
                    }
                }
            }
           throw Throwable("could not decode CWT")
        } catch (e: Throwable) {
            throw e
        }
    }

    private fun getContents(it: CBORObject) = try {
        it.GetByteString()
    } catch (e: Throwable) {
        it.EncodeToBytes()
    }

}