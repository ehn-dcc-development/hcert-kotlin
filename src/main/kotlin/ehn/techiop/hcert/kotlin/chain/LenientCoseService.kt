package ehn.techiop.hcert.kotlin.chain

import COSE.Attribute
import COSE.MessageTag
import COSE.Sign1Message

/**
 * Does not verify the signature on [decode]
 */
class LenientCoseService(private val cryptoService: CryptoService) : CoseService {

    override fun encode(input: ByteArray): ByteArray {
        return Sign1Message().also {
            it.SetContent(input)
            for (header in cryptoService.getCborHeaders()) {
                it.addAttribute(header.first, header.second, Attribute.PROTECTED)
            }
            it.sign(cryptoService.getCborSigningKey())
        }.EncodeToBytes()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        val decoded = Sign1Message.DecodeFromBytes(input, MessageTag.Sign1) as Sign1Message
        verificationResult.coseVerified = false
        return decoded.GetContent()
    }

}