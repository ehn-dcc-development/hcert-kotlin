package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.VerificationResult

class DefualtCoseService :CoseService {
    override fun encode(input: ByteArray): ByteArray {
        TODO("Not yet implemented")
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        TODO("Not yet implemented")
    }
}