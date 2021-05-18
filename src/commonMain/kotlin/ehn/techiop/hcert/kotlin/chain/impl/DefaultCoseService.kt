package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.CryptoService
import kotlinx.serialization.ExperimentalSerializationApi


/**
 * Encodes/decodes input as a Sign1Message according to COSE specification (RFC8152)
 */
@OptIn(ExperimentalSerializationApi::class)
expect class DefaultCoseService constructor(cryptoService: CryptoService) : CoseService {

}
