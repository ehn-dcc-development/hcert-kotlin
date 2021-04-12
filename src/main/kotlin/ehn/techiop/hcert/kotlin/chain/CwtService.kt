package ehn.techiop.hcert.kotlin.chain

import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.cwt.CwtHeaderKeys
import java.time.Instant
import java.time.temporal.ChronoUnit

class CwtService {

    fun wrapPayload(input: ByteArray): ByteArray? {
        val issueTime = Instant.now()
        val expirationTime = issueTime.plus(365, ChronoUnit.DAYS)
        return CBORObject.NewMap().also { payload ->
            payload[CwtHeaderKeys.ISSUER.AsCBOR()] = CBORObject.FromObject("AT")
            payload[CwtHeaderKeys.ISSUED_AT.AsCBOR()] = CBORObject.FromObject(issueTime.epochSecond)
            payload[CwtHeaderKeys.EXPIRATION.AsCBOR()] = CBORObject.FromObject(expirationTime.epochSecond)
            payload[CwtHeaderKeys.HCERT.AsCBOR()] = CBORObject.NewMap().also {
                it[CBORObject.FromObject(1)] = CBORObject.FromObject(input)
            }
        }.EncodeToBytes()
    }


    fun unwrapPayload(input: ByteArray): ByteArray {
        val map = CBORObject.DecodeFromBytes(input)
        val issuer = map[CwtHeaderKeys.ISSUER.AsCBOR()].AsString()
        if (issuer != "AT") throw IllegalArgumentException("Issuer not correct: $issuer")
        val issuedAt = Instant.ofEpochSecond(map[CwtHeaderKeys.ISSUED_AT.AsCBOR()].AsInt64())
        if (issuedAt.isAfter(Instant.now())) throw IllegalArgumentException("IssuedAt not correct: $issuedAt")
        val expirationTime = Instant.ofEpochSecond(map[CwtHeaderKeys.EXPIRATION.AsCBOR()].AsInt64())
        if (expirationTime.isBefore(Instant.now())) throw IllegalArgumentException("Expiration not correct: $expirationTime")
        val hcert = map[CwtHeaderKeys.HCERT.AsCBOR()]
        return hcert[CBORObject.FromObject(1)].GetByteString()
    }

}