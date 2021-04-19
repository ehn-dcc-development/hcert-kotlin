package ehn.techiop.hcert.kotlin.chain.impl

import COSE.Attribute
import COSE.HeaderKeys
import COSE.MessageTag
import COSE.Sign1Message
import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult

open class DefaultCoseService(private val cryptoService: CryptoService) : CoseService {

    override fun encode(input: ByteArray): ByteArray {
        return Sign1Message().also {
            it.SetContent(input)
            for (header in cryptoService.getCborHeaders()) {
                //TODO separate class that puts it into UNPROTECTED for testing issues
                it.addAttribute(header.first, header.second, Attribute.PROTECTED)
            }
            it.sign(cryptoService.getCborSigningKey())
        }.EncodeToBytes()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.coseVerified = false
        return try {
            (Sign1Message.DecodeFromBytes(input, MessageTag.Sign1) as Sign1Message).also {
                getKid(it)?.let { kid ->
                    try {
                        val verificationKey = cryptoService.getCborVerificationKey(kid)
                        verificationResult.coseVerified = it.validate(verificationKey)
                    } catch (e: Throwable) {
                        it.GetContent()
                    }
                }
            }.GetContent()
        } catch (e: Throwable) {
            input
        }
    }

    private fun getKid(it: Sign1Message): ByteArray? {
        val key = HeaderKeys.KID.AsCBOR()
        if (it.protectedAttributes.ContainsKey(key)) {
            return it.protectedAttributes.get(key).GetByteString()
        } else if (it.unprotectedAttributes.ContainsKey(key)) {
            return it.unprotectedAttributes.get(key).GetByteString()
        }
        return null
    }

}