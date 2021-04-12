package ehn.techiop.hcert.kotlin.chain

import COSE.Attribute
import COSE.MessageTag
import COSE.Sign1Message

/**
 * Does not verify the signature on [decode]
 */
class LenientCborService(private val cryptoService: CryptoService) : CborService {

    private val cwtService = CwtService()

    override fun sign(input: ByteArray): ByteArray {
        return Sign1Message().also {
            it.SetContent(cwtService.wrapPayload(input))
            for (header in cryptoService.getCborHeaders()) {
                it.addAttribute(header.first, header.second, Attribute.PROTECTED)
            }
            it.sign(cryptoService.getCborSigningKey())
        }.EncodeToBytes()
    }

    override fun verify(input: ByteArray): ByteArray {
        val decoded = Sign1Message.DecodeFromBytes(input, MessageTag.Sign1) as Sign1Message
        return cwtService.unwrapPayload(decoded.GetContent())
    }

}