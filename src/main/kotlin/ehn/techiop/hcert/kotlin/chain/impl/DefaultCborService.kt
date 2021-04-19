package ehn.techiop.hcert.kotlin.chain.impl

import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CborService
import ehn.techiop.hcert.kotlin.chain.Person
import ehn.techiop.hcert.kotlin.chain.Test
import ehn.techiop.hcert.kotlin.chain.VaccinationData
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.cwt.CwtHeaderKeys
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import java.time.Instant
import java.time.Period

open class DefaultCborService(
    private val countryCode: String = "AT",
    private val expirationPeriod: Period = Period.ofDays(365)
) : CborService {

    private val keyEuDgcV1 = CBORObject.FromObject(1)

    override fun encode(input: VaccinationData): ByteArray {
        val cbor = Cbor { ignoreUnknownKeys = true }.encodeToByteArray(input)
        val issueTime = Instant.now()
        val expirationTime = issueTime + expirationPeriod
        return CBORObject.NewMap().also {
            it[CwtHeaderKeys.ISSUER.AsCBOR()] = CBORObject.FromObject(countryCode)
            it[CwtHeaderKeys.ISSUED_AT.AsCBOR()] = CBORObject.FromObject(issueTime.epochSecond)
            it[CwtHeaderKeys.EXPIRATION.AsCBOR()] = CBORObject.FromObject(expirationTime.epochSecond)
            it[CwtHeaderKeys.HCERT.AsCBOR()] = CBORObject.NewMap().also {
                it[keyEuDgcV1] = CBORObject.FromObject(cbor)
            }
        }.EncodeToBytes()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): VaccinationData {
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
                    return Cbor { ignoreUnknownKeys = true }
                        .decodeFromByteArray<VaccinationData>(it.GetByteString())
                        .also { verificationResult.cborDecoded = true }
                }
            }

            map["@context"]?.let { // NL from https://demo.uvci.eu/
                val name = map["https://schema.org/nam"]?.AsString()
                val gender = map["https://schema.org/gen"]?.AsString()
                val date = map["https://schema.org/dat"]?.AsString()
                return VaccinationData(
                    Person(givenName = name, gender = gender),
                    tests = listOf(Test(dateTimeSample = date))
                ).also { verificationResult.cborDecoded = true }
            }

            return VaccinationData()
        } catch (e: Throwable) {
            return VaccinationData()
        }
    }

}