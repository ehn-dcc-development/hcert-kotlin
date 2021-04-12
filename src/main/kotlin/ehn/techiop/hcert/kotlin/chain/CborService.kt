package ehn.techiop.hcert.kotlin.chain

import COSE.Attribute
import COSE.HeaderKeys
import COSE.MessageTag
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.cwt.CwtHeaderKeys
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import java.time.Instant
import java.time.temporal.ChronoUnit

class CborService(private val cryptoService: CryptoService) {

    fun sign(input: ByteArray): ByteArray {
        return Sign1Message().also {
            it.SetContent(wrapPayload(input))
            for (header in cryptoService.getCborHeaders()) {
                it.addAttribute(header.first, header.second, Attribute.PROTECTED)
            }
            it.sign(cryptoService.getCborSigningKey())
        }.EncodeToBytes()
    }

    fun verify(input: ByteArray): ByteArray {
        val decoded = Sign1Message.DecodeFromBytes(input, MessageTag.Sign1) as Sign1Message
        val kid = decoded.protectedAttributes.get(HeaderKeys.KID.AsCBOR()).AsString()
        if (!decoded.validate(cryptoService.getCborVerificationKey(kid)))
            throw IllegalArgumentException("Not validated")
        return unwrapPayload(decoded.GetContent())
    }

    inline fun <reified T> encode(input: T): ByteArray {
        return Cbor { ignoreUnknownKeys = true }.encodeToByteArray(input)
    }

    inline fun <reified T> decode(input: ByteArray): T {
        return Cbor { ignoreUnknownKeys = true }.decodeFromByteArray<T>(input)
    }

    private fun wrapPayload(input: ByteArray): ByteArray? {
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


    private fun unwrapPayload(input: ByteArray): ByteArray {
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