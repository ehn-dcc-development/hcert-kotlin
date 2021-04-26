package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.data.Eudgc

interface CborService {

    fun encode(input: Eudgc): ByteArray

    fun decode(input: ByteArray, verificationResult: VerificationResult): Eudgc

}