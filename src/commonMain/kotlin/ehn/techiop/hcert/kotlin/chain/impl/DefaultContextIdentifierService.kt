package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.ContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.VerificationResult

/**
 * Appends/drops the Context identifier prefix from input, e.g. "HC1:"
 */
open class DefaultContextIdentifierService(private val prefix: String = "HC1:") : ContextIdentifierService {

    override fun encode(input: String): String {
        return "$prefix$input"
    }

    override fun decode(input: String, verificationResult: VerificationResult): String = when {
        input.startsWith(prefix) -> input.drop(prefix.length)
        else -> input.also { verificationResult.error = VerificationResult.Error.CONTEXT_IDENTIFIER_INVALID }
    }

}