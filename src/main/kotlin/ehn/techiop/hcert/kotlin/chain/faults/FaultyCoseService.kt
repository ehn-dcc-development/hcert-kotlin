package ehn.techiop.hcert.kotlin.chain.faults

import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService

/**
 * Encodes the input into an non-parsable COSE structure (i.e. reversed)
 */
class FaultyCoseService(private val cryptoService: CryptoService) : DefaultCoseService(cryptoService) {

    override fun encode(input: ByteArray): ByteArray {
        return super.encode(input).reversedArray()
    }

}