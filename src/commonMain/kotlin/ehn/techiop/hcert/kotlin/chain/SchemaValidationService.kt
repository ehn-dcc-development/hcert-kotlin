package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.data.GreenCertificate

interface SchemaValidationService {
    /**
     * Throws a Throwable if schema validation fails
     */
    fun validate(cbor: ByteArray, verificationResult: VerificationResult)
}