package ehn.techiop.hcert.kotlin.chain.faults

import ehn.techiop.hcert.kotlin.chain.ValSuiteService
import ehn.techiop.hcert.kotlin.chain.VerificationResult

class FaultyValSuiteService(private val prefix: String = "HC1") : ValSuiteService {

    override fun encode(input: String): String {
        return input;
    }

    override fun decode(input: String, verificationResult: VerificationResult): String = when {
        input.startsWith(prefix) -> input.drop(prefix.length).also { verificationResult.valSuitePrefix = prefix }
        else -> input.also { verificationResult.valSuitePrefix = null }
    }

}