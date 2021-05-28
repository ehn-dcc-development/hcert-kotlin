package ehn.techiop.hcert.kotlin.chain.impl

import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CwtService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import java.time.Clock
import java.time.Duration
import java.time.Instant

/**
 * Encodes/decodes input as a CWT structure, ready to sign with COSE
 */
open class DefaultCwtService(
    private val countryCode: String = "AT",
    private val validity: Duration = Duration.ofHours(48),
    private val clock: Clock = Clock.systemUTC(),
) : CwtService {

    private val keyEuDgcV1 = CBORObject.FromObject(1)

    override fun encode(input: ByteArray): ByteArray {
        val issueTime = clock.instant()
        val expirationTime = issueTime + validity
        return CBORObject.NewMap().also {
            it[CwtHeaderKeys.ISSUER.AsCBOR()] = CBORObject.FromObject(countryCode)
            it[CwtHeaderKeys.ISSUED_AT.AsCBOR()] = CBORObject.FromObject(issueTime.epochSecond)
            it[CwtHeaderKeys.EXPIRATION.AsCBOR()] = CBORObject.FromObject(expirationTime.epochSecond)
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
                verificationResult.issuedAt = Instant.ofEpochSecond(it.AsInt64())
            }
            map[CwtHeaderKeys.EXPIRATION.AsCBOR()]?.let {
                verificationResult.expirationTime = Instant.ofEpochSecond(it.AsInt64())
            }

            map[CwtHeaderKeys.HCERT.AsCBOR()]?.let { hcert -> // SPEC
                hcert[keyEuDgcV1]?.let { eudccV1 ->
                    return getContents(eudccV1).also {
                        verificationResult.cwtDecoded = true
                    }
                }
            }
            return input
        } catch (e: Throwable) {
            return input
        }
    }

    private fun getContents(it: CBORObject) = try {
        it.GetByteString()
    } catch (e: Throwable) {
        it.EncodeToBytes()
    }

}