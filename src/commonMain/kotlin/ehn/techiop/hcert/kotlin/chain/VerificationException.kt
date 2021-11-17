package ehn.techiop.hcert.kotlin.chain

open class VerificationException(
    val error: Error,
    message: String? = null,
    cause: Throwable? = null,
    val details: ErrorDetails? = null
) : Exception(message, cause)

class NonFatalVerificationException(val result:Any,error: Error,message: String?=null,cause: Throwable?=null,details: ErrorDetails?=null):VerificationException(error, message, cause, details)
