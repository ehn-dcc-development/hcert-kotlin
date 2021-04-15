package ehn.techiop.hcert.kotlin.chain.faults

import COSE.Attribute
import COSE.HeaderKeys
import COSE.MessageTag
import COSE.Sign1Message
import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult

class FaultyCoseService(private val cryptoService: CryptoService) : CoseService {

    override fun encode(input: ByteArray): ByteArray {
        return Sign1Message().also {
            it.SetContent(input)
            for (header in cryptoService.getCborHeaders()) {
                it.addAttribute(header.first, header.second, Attribute.PROTECTED)
            }
            it.sign(RandomEcKeyCryptoService().getCborSigningKey())
        }.EncodeToBytes()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.coseVerified = false
        return try {
            (Sign1Message.DecodeFromBytes(input, MessageTag.Sign1) as Sign1Message).also {
                getKid(it)?.let { kid ->
                    verificationResult.coseVerified = it.validate(cryptoService.getCborVerificationKey(kid))
                }
            }.GetContent()
        } catch (e: Throwable) {
            input
        }
    }

    private fun getKid(it: Sign1Message): String? {
        if (it.protectedAttributes.ContainsKey(HeaderKeys.KID.AsCBOR())) {
            return it.protectedAttributes.get(HeaderKeys.KID.AsCBOR()).AsString()
        } else if (it.unprotectedAttributes.ContainsKey(HeaderKeys.KID.AsCBOR())) {
            return it.unprotectedAttributes.get(HeaderKeys.KID.AsCBOR()).AsString()
        }
        return null
    }

}