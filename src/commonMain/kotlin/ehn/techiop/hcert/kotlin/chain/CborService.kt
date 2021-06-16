package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.data.CborObject
import ehn.techiop.hcert.kotlin.data.GreenCertificate

/**
 * Encodesinput as a CBOR structure
 */
interface CborService {

    fun encode(input: GreenCertificate): ByteArray
}
