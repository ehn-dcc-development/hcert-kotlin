package ehn.techiop.hcert.kotlin.chain.impl

import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CwtService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
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
        return CBORObject.NewMap().also {
            it[CwtHeaderKeys.ISSUER.value] = CBORObject.FromObject(countryCode)
            it[CwtHeaderKeys.ISSUED_AT.value] = CBORObject.FromObject(issueTime.epochSeconds)
            it[CwtHeaderKeys.EXPIRATION.value] = CBORObject.FromObject(expirationTime.epochSeconds)
            it[CwtHeaderKeys.HCERT.value] = CBORObject.NewMap().also { hcert ->
                try {
                    hcert[CwtHeaderKeys.EUDGC_IN_HCERT.value] = CBORObject.DecodeFromBytes(input)
                } catch (e: Throwable) {
                    hcert[CwtHeaderKeys.EUDGC_IN_HCERT.value] = CBORObject.FromObject(input)
                }
            }
        }.EncodeToBytes()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.cwtDecoded = false
        val map = CBORObject.DecodeFromBytes(input)

        map[CwtHeaderKeys.ISSUER.value]?.let {
            verificationResult.issuer = it.AsString()
        }
        map[CwtHeaderKeys.ISSUED_AT.value]?.let {
            verificationResult.issuedAt = Instant.fromEpochSeconds(it.AsInt64())
        }
        map[CwtHeaderKeys.EXPIRATION.value]?.let {
            verificationResult.expirationTime = Instant.fromEpochSeconds(it.AsInt64())
        }

        map[CwtHeaderKeys.HCERT.value]?.let { hcert -> // SPEC
            hcert[CwtHeaderKeys.EUDGC_IN_HCERT.value]?.let { eudgcV1 ->
                return getContents(eudgcV1).also {
                    verificationResult.cwtDecoded = true
                }
            }
        }
        throw Throwable("could not decode CWT")
    }

    private fun getContents(it: CBORObject) = try {
        it.GetByteString()
    } catch (e: Throwable) {
        it.EncodeToBytes()
    }

}