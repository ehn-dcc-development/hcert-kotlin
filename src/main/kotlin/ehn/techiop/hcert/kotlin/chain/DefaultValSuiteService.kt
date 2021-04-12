package ehn.techiop.hcert.kotlin.chain

/**
 * Appends/drops a country-specific prefix from contents, e.g. "AT01"
 */
class DefaultValSuiteService(private val prefix: String = "AT01") : ValSuiteService {

    override fun encode(input: String): String {
        return "$prefix$input";
    }

    override fun decode(input: String): String {
        if (!input.startsWith(prefix)) throw IllegalArgumentException("Prefix not in input: $prefix")
        return input.drop(prefix.length)
    }

}