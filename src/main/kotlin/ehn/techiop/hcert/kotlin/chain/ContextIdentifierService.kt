package ehn.techiop.hcert.kotlin.chain


/**
 * Appends/drops an Context identifier prefix from input
 */
interface ContextIdentifierService {

    fun encode(input: String): String

    fun decode(input: String, verificationResult: VerificationResult): String

}