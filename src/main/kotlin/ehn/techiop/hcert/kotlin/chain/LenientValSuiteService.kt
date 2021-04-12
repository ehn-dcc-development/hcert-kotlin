package ehn.techiop.hcert.kotlin.chain

/**
 * Appends and *optionally* drops a country-specific prefix from contents, e.g. "AT01"
 */
class LenientValSuiteService(private val prefix: String = "AT01") : ValSuiteService {

    override fun encode(input: String): String {
        return "$prefix$input";
    }

    override fun decode(input: String, verificationResult: VerificationResult): String = when {
        input.startsWith(prefix) -> input.drop(prefix.length).also { verificationResult.valSuitePrefix = prefix }
        else -> input.also { verificationResult.valSuitePrefix = null }
    }

}