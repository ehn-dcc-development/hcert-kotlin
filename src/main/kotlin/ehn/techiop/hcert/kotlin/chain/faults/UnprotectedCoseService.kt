package ehn.techiop.hcert.kotlin.chain.faults

import COSE.Attribute
import COSE.HeaderKeys
import COSE.Sign1Message
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService

/**
 * Puts the KID header entry into the unprotected COSE header.
 *
 * Actually, this conforms to the specification, but we'll prefer to put the entries into the protected COSE header.
 */
class UnprotectedCoseService(private val cryptoService: CryptoService) : DefaultCoseService(cryptoService) {

    override fun encode(input: ByteArray): ByteArray {
        return Sign1Message().also {
            it.SetContent(input)
            for (header in cryptoService.getCborHeaders()) {
                if (header.first == HeaderKeys.KID) {
                    it.addAttribute(header.first, header.second, Attribute.UNPROTECTED)
                } else {
                    it.addAttribute(header.first, header.second, Attribute.PROTECTED)
                }
            }
            it.sign(cryptoService.getCborSigningKey())
        }.EncodeToBytes()
    }

}