package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.ContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.VerificationResult

/**
 * Appends/drops the Context identifier prefix from contents, e.g. "HC1:"
 */
open class DefaultContextIdentifierService(private val prefix: String = "HC1:") : ContextIdentifierService {

    override fun encode(input: String): String {
        return "$prefix$input"
    }

    override fun decode(input: String, verificationResult: VerificationResult): String = when {
        input.startsWith(prefix) -> input.drop(prefix.length).also { verificationResult.contextIdentifier = prefix }
        else -> input.also { verificationResult.contextIdentifier = null }
    }

}