package ehn.techiop.hcert.kotlin.chain.faults

import ehn.techiop.hcert.kotlin.chain.impl.DefaultValSuiteService

class FaultyValSuiteService(private val prefix: String = "HC1") : DefaultValSuiteService(prefix) {

    override fun encode(input: String): String {
        return input
    }

}