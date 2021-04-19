package ehn.techiop.hcert.kotlin.chain.faults

import ehn.techiop.hcert.kotlin.chain.impl.DefaultContextIdentifierService

class NoopContextIdentifierService(prefix: String = "HC1:") : DefaultContextIdentifierService(prefix) {

    override fun encode(input: String): String {
        return input
    }

}