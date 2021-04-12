package ehn.techiop.hcert.kotlin.chain

interface ValSuiteService {

    fun encode(input: String): String

    fun decode(input: String): String

}