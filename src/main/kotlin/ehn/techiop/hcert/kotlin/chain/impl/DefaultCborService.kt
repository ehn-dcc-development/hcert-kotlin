package ehn.techiop.hcert.kotlin.chain.impl

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.data.DigitalGreenCertificate
import ehn.techiop.hcert.data.Sub
import ehn.techiop.hcert.kotlin.chain.CborService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.cwt.CwtHeaderKeys
import java.time.Instant
import java.time.Period
import java.util.Optional

open class DefaultCborService(
    private val countryCode: String = "AT",
    private val expirationPeriod: Period = Period.ofDays(365)
) : CborService {

    private val keyEuDgcV1 = CBORObject.FromObject(1)

    override fun encode(input: DigitalGreenCertificate): ByteArray {
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

    override fun decode(input: ByteArray, verificationResult: VerificationResult): DigitalGreenCertificate {
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
                    return CBORMapper()
                        .readValue(getContents(it), DigitalGreenCertificate::class.java)
                        .also { verificationResult.cborDecoded = true }
                }
            }

            map["@context"]?.let { // NL from https://demo.uvci.eu/
                val name = map["https://schema.org/nam"]?.AsString()
                //val gender = map["https://schema.org/gen"]?.AsString()
                //val date = map["https://schema.org/dat"]?.AsInt32()
                return DigitalGreenCertificate().apply {
                    sub = Sub().apply {
                        gn = name
                        //gen = Optional.of(Sub.AdministrativeGender.UNKNOWN)
                    }
                    //tst = listOf(Tst().apply {
                    //    dts = date
                    //})
                }.also { verificationResult.cborDecoded = true }
            }

            return DigitalGreenCertificate()
        } catch (e: Throwable) {
            return DigitalGreenCertificate()
        }
    }

    private fun getContents(it: CBORObject) = try {
        it.GetByteString()
    } catch (e: Throwable) {
        it.EncodeToBytes()
    }

}