package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.data.Eudgc

/**
 * Encodes/decodes input as a CWT structure, ready to sign with COSE
 */
interface CwtService {

    fun encode(input: Eudgc): ByteArray

    fun decode(input: ByteArray, verificationResult: VerificationResult): Eudgc

}