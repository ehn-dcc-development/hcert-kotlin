package ehn.techiop.hcert.kotlin.chain

class VerificationException(
    val error: Error,
    message: String? = null,
    cause: Throwable? = null,
    val details: Map<String, String>? = null
) : Exception(message, cause)
