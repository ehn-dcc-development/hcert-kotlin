package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.data.Eudcc

/**
 * Encodes/decodes input as a CBOR structure
 */
interface CborService {

    fun encode(input: Eudcc): ByteArray

    fun decode(input: ByteArray, verificationResult: VerificationResult): Eudcc

}