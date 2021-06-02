package ehn.techiop.hcert.kotlin.chain

interface SchemaValidationService {
    /**
     * Throws a Throwable if schema validation fails
     */
    fun validate(cbor: ByteArray, verificationResult: VerificationResult)
}