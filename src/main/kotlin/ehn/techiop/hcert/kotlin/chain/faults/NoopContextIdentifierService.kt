package ehn.techiop.hcert.kotlin.chain.faults

import ehn.techiop.hcert.kotlin.chain.impl.DefaultContextIdentifierService

/**
 * Does not add a prefix to the input, thus violates the specification.
 *
 * **Should not be used in production.**
 */
class NoopContextIdentifierService(prefix: String = "HC1:") : DefaultContextIdentifierService(prefix) {

    override fun encode(input: String): String {
        return input
    }

}