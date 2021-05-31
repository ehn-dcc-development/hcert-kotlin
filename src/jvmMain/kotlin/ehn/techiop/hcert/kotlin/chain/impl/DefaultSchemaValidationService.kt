package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.SchemaValidationService
import ehn.techiop.hcert.kotlin.chain.VerificationResult

actual class DefaultSchemaValidationService : SchemaValidationService {

    override fun validate(cbor: ByteArray, verificationResult: VerificationResult) {
        verificationResult.schemaValidated = true
        //TODO Implement Schema validation on JVM
    }

}