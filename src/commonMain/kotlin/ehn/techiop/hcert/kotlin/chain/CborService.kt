package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.data.GreenCertificate

/**
 * Encodes/decodes input as a CBOR structure
 */
interface CborService {

    fun encode(input: GreenCertificate): ByteArray

    fun decode(input: ByteArray, verificationResult: VerificationResult): GreenCertificate?

}