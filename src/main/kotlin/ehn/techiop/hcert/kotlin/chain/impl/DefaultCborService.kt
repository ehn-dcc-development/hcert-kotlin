package ehn.techiop.hcert.kotlin.chain.impl

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.data.Eudgc
import ehn.techiop.hcert.kotlin.chain.CborService
import ehn.techiop.hcert.kotlin.trust.ContentType
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import java.time.Instant
import java.time.Period

open class DefaultCborService(
    private val countryCode: String = "AT",
    private val expirationPeriod: Period = Period.ofDays(2)
) : CborService {

    private val keyEuDgcV1 = CBORObject.FromObject(1)

    override fun encode(input: Eudgc): ByteArray {
        val cbor = CBORMapper().writeValueAsBytes(input)
        val issueTime = Instant.now()
        val expirationTime = issueTime + expirationPeriod
        return CBORObject.NewMap().also {
            it[CwtHeaderKeys.ISSUER.AsCBOR()] = CBORObject.FromObject(countryCode)
            it[CwtHeaderKeys.ISSUED_AT.AsCBOR()] = CBORObject.FromObject(issueTime.epochSecond)
            it[CwtHeaderKeys.EXPIRATION.AsCBOR()] = CBORObject.FromObject(expirationTime.epochSecond)
            it[CwtHeaderKeys.HCERT.AsCBOR()] = CBORObject.NewMap().also {
                it[keyEuDgcV1] = CBORObject.DecodeFromBytes(cbor)
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
                hcert[keyEuDgcV1]?.let {
                    val eudgc = CBORMapper()
                        .readValue(getContents(it), Eudgc::class.java)
                        .also { verificationResult.cborDecoded = true }
                    if (eudgc.t?.filterNotNull()?.isNotEmpty() == true)
                        verificationResult.content.add(ContentType.TEST)
                    if (eudgc.v?.filterNotNull()?.isNotEmpty() == true)
                        verificationResult.content.add(ContentType.VACCINATION)
                    if (eudgc.r?.filterNotNull()?.isNotEmpty() == true)
                        verificationResult.content.add(ContentType.RECOVERY)
                    return eudgc
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