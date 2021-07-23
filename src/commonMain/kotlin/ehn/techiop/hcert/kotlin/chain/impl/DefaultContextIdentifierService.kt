package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.ContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult

/**
 * Appends/drops the Context identifier prefix from input, e.g. "HC1:"
 */
open class DefaultContextIdentifierService(private val prefix: String = "HC1:") : ContextIdentifierService {

    override fun encode(input: String): String {
        return "$prefix$input"
    }

    override fun decode(input: String, verificationResult: VerificationResult) = when {
        input.startsWith(prefix) -> input.drop(prefix.length)
        else -> throw VerificationException(
            Error.INVALID_SCHEME_PREFIX,
            "No context prefix '$prefix'",
            details = mapOf("prefix" to prefix)
        )
    }

}
