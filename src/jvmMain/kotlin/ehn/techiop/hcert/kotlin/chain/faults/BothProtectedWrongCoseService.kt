package ehn.techiop.hcert.kotlin.chain.faults

import COSE.Attribute
import COSE.HeaderKeys
import COSE.OneKey
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService

/**
 * Puts wrong header entries into the protected COSE header, plus an additional unprotected KID with correct value.
 */
class BothProtectedWrongCoseService(private val cryptoService: CryptoService) :
    DefaultCoseService(cryptoService) {

    override fun encode(input: ByteArray): ByteArray {
        return Sign1Message().also {
            it.SetContent(input)
            for (header in cryptoService.getCborHeaders()) {
                it.addAttribute(CBORObject.FromObject(header.first), CBORObject.FromObject(header.second), Attribute.UNPROTECTED)
            }
            it.protectedAttributes.Add(HeaderKeys.KID.AsCBOR(), CBORObject.FromObject("foo".toByteArray()))
            it.sign(cryptoService.getCborSigningKey().toCoseRepresentation() as OneKey)
        }.EncodeToBytes()
    }

}