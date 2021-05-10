package ehn.techiop.hcert.kotlin.chain.impl

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.data.Eudgc
import ehn.techiop.hcert.kotlin.chain.CwtService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.trust.ContentType
import java.time.Clock
import java.time.Duration
import java.time.Instant

/**
 * Encodes/decodes input as a CWT structure, ready to sign with COSE
 */
open class DefaultCwtService(
    private val countryCode: String = "AT",
    private val validity: Duration = Duration.ofHours(48),
    private val clock: Clock = Clock.systemDefaultZone()
) : CwtService {

    private val keyEuDgcV1 = CBORObject.FromObject(1)

    override fun encode(input: Eudgc): ByteArray {
        val cbor = CBORMapper().writeValueAsBytes(input)
        val issueTime = clock.instant()
        val expirationTime = issueTime + validity
        return CBORObject.NewMap().also {
            it[CwtHeaderKeys.ISSUER.AsCBOR()] = CBORObject.FromObject(countryCode)
            it[CwtHeaderKeys.ISSUED_AT.AsCBOR()] = CBORObject.FromObject(issueTime.epochSecond)
            it[CwtHeaderKeys.EXPIRATION.AsCBOR()] = CBORObject.FromObject(expirationTime.epochSecond)
            it[CwtHeaderKeys.HCERT.AsCBOR()] = CBORObject.NewMap().also { hcert ->
                hcert[keyEuDgcV1] = CBORObject.DecodeFromBytes(cbor)
            }
        }.EncodeToBytes()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): Eudgc {
        verificationResult.cborDecoded = false
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
                hcert[keyEuDgcV1]?.let { eudgcV1 ->
                    return CBORMapper()
                        .readValue(getContents(eudgcV1), Eudgc::class.java)
                        .also { result ->
                            verificationResult.cborDecoded = true
                            if (result.t?.filterNotNull()?.isNotEmpty() == true)
                                verificationResult.content.add(ContentType.TEST)
                            if (result.v?.filterNotNull()?.isNotEmpty() == true)
                                verificationResult.content.add(ContentType.VACCINATION)
                            if (result.r?.filterNotNull()?.isNotEmpty() == true)
                                verificationResult.content.add(ContentType.RECOVERY)
                        }
                }
            }
            return Eudgc()
        } catch (e: Throwable) {
            return Eudgc()
        }
    }

    private fun getContents(it: CBORObject) = try {
        it.GetByteString()
    } catch (e: Throwable) {
        it.EncodeToBytes()
    }

}