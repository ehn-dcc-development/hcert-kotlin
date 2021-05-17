package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.CryptoService


/**
 * Encodes/decodes input as a Sign1Message according to COSE specification (RFC8152)
 */
expect class DefaultCoseService : CoseService {
    companion object {
        fun getInstance(cryptoService: CryptoService): DefaultCoseService
    }
}
