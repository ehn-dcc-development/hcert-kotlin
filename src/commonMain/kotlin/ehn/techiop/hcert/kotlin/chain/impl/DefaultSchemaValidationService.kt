package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.SchemaValidationService
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.data.CborObject
import ehn.techiop.hcert.kotlin.data.GreenCertificate

class DefaultSchemaValidationService : SchemaValidationService {

    override fun validate(cbor: CborObject, verificationResult: VerificationResult): GreenCertificate {
        val adapter = SchemaValidationAdapter(cbor)

        val versionString = cbor.getVersionString() ?: throw VerificationException(
            Error.CBOR_DESERIALIZATION_FAILED,
            "No schema version specified"
        )
        if (!adapter.hasValidator(versionString)) throw VerificationException(
            Error.SCHEMA_VALIDATION_FAILED,
            "Schema version $versionString is not supported"
        )

        val errors = adapter.validateBasic(versionString)
        if (errors.isNotEmpty()) {
            val fallbackErrors = adapter.validateWithFallback()
            if (fallbackErrors.isNotEmpty()) throw VerificationException(
                Error.SCHEMA_VALIDATION_FAILED,
                "Data does not follow fallback schema: $fallbackErrors}"
            )
        }
        return adapter.toJson()
    }

}


