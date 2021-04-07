package ehn.techiop.hcert.kotlin

import COSE.Attribute
import COSE.HeaderKeys
import COSE.MessageTag
import COSE.Sign1Message
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray

class CborService(private val cryptoService: CryptoService) {

    fun sign(input: ByteArray): ByteArray {
        return Sign1Message().also {
            it.SetContent(input)
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
        return decoded.GetContent()
    }

    inline fun <reified T> encode(input: T): ByteArray {
        return Cbor.encodeToByteArray(input)
    }

    inline fun <reified T> decode(input: ByteArray): T {
        return Cbor.decodeFromByteArray<T>(input)
    }

}