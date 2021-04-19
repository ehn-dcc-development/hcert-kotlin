package ehn.techiop.hcert.kotlin.chain

interface ContextIdentifierService {

    fun encode(input: String): String

    fun decode(input: String, verificationResult: VerificationResult): String

}