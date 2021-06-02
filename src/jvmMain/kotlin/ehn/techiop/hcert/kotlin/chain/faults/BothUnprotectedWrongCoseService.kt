package ehn.techiop.hcert.kotlin.chain.faults

import COSE.Attribute
import COSE.HeaderKeys
import COSE.OneKey
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService

/**
 * Puts header entries into the protected COSE header, plus an additional unprotected KID with wrong value.
 *
 * Actually, this conforms to the specification, but we'll prefer to put the entries into the protected COSE header.
 */
class BothUnprotectedWrongCoseService(private val cryptoService: CryptoService) : DefaultCoseService(cryptoService) {

    override fun encode(input: ByteArray): ByteArray {
        return Sign1Message().also {
            it.SetContent(input)
            for (header in cryptoService.getCborHeaders()) {
                it.addAttribute(
                    CBORObject.FromObject(header.first.value),
                    CBORObject.FromObject(header.second),
                    Attribute.PROTECTED
                )
            }
            it.unprotectedAttributes.Add(HeaderKeys.KID.AsCBOR(), CBORObject.FromObject("foo".toByteArray()))
            it.sign(cryptoService.getCborSigningKey().toCoseRepresentation() as OneKey)
        }.EncodeToBytes()
    }

}