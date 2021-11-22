package ehn.techiop.hcert.kotlin.chain.debug

import ehn.techiop.hcert.kotlin.chain.*
import kotlin.jvm.JvmOverloads

/**
 * Appends/drops the Context identifier prefix from input, e.g. "HC1:"
 */
open class DebugContextIdentifierService @JvmOverloads constructor(private val prefix: String = "HC1:") :
    ContextIdentifierService {

    override fun encode(input: String): String {
        return "$prefix$input"
    }

    override fun decode(input: String, verificationResult: VerificationResult) = when {
        input.startsWith(prefix) -> input.drop(prefix.length)
        else -> throw NonFatalVerificationException(
            input,
            Error.INVALID_SCHEME_PREFIX,
            "No context prefix '$prefix'",
            details = mapOf("prefix" to prefix)
        )
    }

}
