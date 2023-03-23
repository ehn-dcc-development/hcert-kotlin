package ehn.techiop.hcert.kotlin.chain.faults

import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.trust.CoseCreationAdapter

/**
 * Puts all header entries into the unprotected COSE header, and garbles the KID
 */
class WrongUnprotectedCoseService(private val cryptoService: CryptoService) : DefaultCoseService(cryptoService) {

    override fun encode(input: ByteArray): ByteArray {
        val coseAdapter = CoseCreationAdapter(input)
        cryptoService.getCborHeaders().forEach {
            if (it.first == CoseHeaderKeys.KID)
                coseAdapter.addUnprotectedAttribute(it.first, "foo".encodeToByteArray())
            else
                coseAdapter.addUnprotectedAttribute(it.first, it.second)
        }
        coseAdapter.sign(cryptoService.getCborSigningKey())
        return coseAdapter.encode()
    }

}