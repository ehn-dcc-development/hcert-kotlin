package ehn.techiop.hcert.kotlin.chain.faults

import COSE.Attribute
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService

/**
 * Signs the input with a random key, i.e. it is never verifiable.
 *
 * **Should not be used in production.**
 */
class NonVerifiableCoseService(private val cryptoService: CryptoService) : DefaultCoseService(cryptoService) {

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
            it.sign(RandomEcKeyCryptoService().getCborSigningKey().oneKey)
        }.EncodeToBytes()
    }

}