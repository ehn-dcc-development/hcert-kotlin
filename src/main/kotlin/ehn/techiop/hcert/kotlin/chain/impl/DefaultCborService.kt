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
import java.time.temporal.ChronoUnit

class DefaultCborService : CborService {

    override fun encode(input: VaccinationData): ByteArray {
        val cbor = Cbor { ignoreUnknownKeys = true }.encodeToByteArray(input)
        val issueTime = Instant.now()
        val expirationTime = issueTime.plus(365, ChronoUnit.DAYS)
        return CBORObject.NewMap().also {
            it[CwtHeaderKeys.ISSUER.AsCBOR()] = CBORObject.FromObject("AT")
            it[CwtHeaderKeys.ISSUED_AT.AsCBOR()] = CBORObject.FromObject(issueTime.epochSecond)
            it[CwtHeaderKeys.EXPIRATION.AsCBOR()] = CBORObject.FromObject(expirationTime.epochSecond)
            it[CwtHeaderKeys.HCERT.AsCBOR()] = CBORObject.NewMap().also {
                it[CBORObject.FromObject(1)] = CBORObject.FromObject(cbor)
            }
        }.EncodeToBytes()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): VaccinationData {
        verificationResult.cborDecoded = false
        try {
            val map = CBORObject.DecodeFromBytes(input)
            map["@context"]?.let { // NL from https://demo.uvci.eu/
                val name = map["https://schema.org/nam"].AsString()
                val gender = map["https://schema.org/gen"].AsString()
                val date = map["https://schema.org/dat"].AsString()
                return VaccinationData(Person(n = name, gen = gender), tst = listOf(Test(dat = date))).also {
                    verificationResult.cborDecoded = true
                }
            }

            val issuer = map[CwtHeaderKeys.ISSUER.AsCBOR()].AsString()
            if (issuer != "AT") throw IllegalArgumentException("Issuer not correct: $issuer")

            val issuedAt = Instant.ofEpochSecond(map[CwtHeaderKeys.ISSUED_AT.AsCBOR()].AsInt64())
            if (issuedAt.isAfter(Instant.now())) throw IllegalArgumentException("IssuedAt not correct: $issuedAt")

            val expirationTime = Instant.ofEpochSecond(map[CwtHeaderKeys.EXPIRATION.AsCBOR()].AsInt64())
            if (expirationTime.isBefore(Instant.now())) throw IllegalArgumentException("Expiration not correct: $expirationTime")

            val hcert = map[CwtHeaderKeys.HCERT.AsCBOR()]
            val hcertv1 = hcert[CBORObject.FromObject(1)].GetByteString()
            return Cbor { ignoreUnknownKeys = true }.decodeFromByteArray<VaccinationData>(hcertv1).also {
                verificationResult.cborDecoded = true
            }
        } catch (e: Throwable) {
            return VaccinationData()
        }
    }

}