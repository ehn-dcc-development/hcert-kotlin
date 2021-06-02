package ehn.techiop.hcert.kotlin.chain.faults

import COSE.Attribute
import COSE.OneKey
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys

/**
 * Puts header entries into the unprotected COSE header.
 *
 * Actually, this conforms to the specification, but we'll prefer to put the entries into the protected COSE header.
 */
class WrongUnprotectedCoseService(private val cryptoService: CryptoService) : DefaultCoseService(cryptoService) {

    override fun encode(input: ByteArray): ByteArray {
        return Sign1Message().also {
            it.SetContent(input)
            for (header in cryptoService.getCborHeaders()) {
                if (header.first == CoseHeaderKeys.KID) {
                    it.addAttribute(
                        CBORObject.FromObject(header.first.intVal),
                        CBORObject.FromObject("foo".toByteArray()),
                        Attribute.UNPROTECTED
                    )
                } else {
                    it.addAttribute(
                        CBORObject.FromObject(header.first.intVal),
                        CBORObject.FromObject(header.second),
                        Attribute.UNPROTECTED
                    )
                }
            }
            it.sign(cryptoService.getCborSigningKey().toCoseRepresentation() as OneKey)
        }.EncodeToBytes()
    }

}