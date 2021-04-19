package ehn.techiop.hcert.kotlin.chain.faults

import COSE.Attribute
import COSE.Sign1Message
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService

/**
 * Puts header entries into the unprotected COSE header
 */
class UnprotectedCoseService(private val cryptoService: CryptoService) : DefaultCoseService(cryptoService) {

    override fun encode(input: ByteArray): ByteArray {
        return Sign1Message().also {
            it.SetContent(input)
            for (header in cryptoService.getCborHeaders()) {
                it.addAttribute(header.first, header.second, Attribute.UNPROTECTED)
            }
            it.sign(cryptoService.getCborSigningKey())
        }.EncodeToBytes()
    }

}