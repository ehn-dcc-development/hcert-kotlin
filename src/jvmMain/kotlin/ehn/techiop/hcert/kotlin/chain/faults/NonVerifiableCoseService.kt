package ehn.techiop.hcert.kotlin.chain.faults

import COSE.Attribute
import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.trust.CoseCreationAdapter

/**
 * Signs the input with a random key, i.e. it is never verifiable.
 *
 * **Should not be used in production.**
 */
class NonVerifiableCoseService(private val cryptoService: CryptoService) : DefaultCoseService(cryptoService) {

    override fun encode(input: ByteArray): ByteArray {
        val coseAdapter = CoseCreationAdapter(input)
        cryptoService.getCborHeaders().forEach {
            coseAdapter.addProtectedAttribute(it.first, it.second)
        }
        coseAdapter.sign(RandomEcKeyCryptoService().getCborSigningKey())
        return coseAdapter.encode()
    }

}