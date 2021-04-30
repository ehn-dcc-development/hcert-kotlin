package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.data.Eudgc

/**
 * Encodes input as a COSE structure
 */
interface CborService {

    fun encode(input: Eudgc): ByteArray

    fun decode(input: ByteArray, verificationResult: VerificationResult): Eudgc

}