package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.SchemaValidationService
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.data.CborObject
import ehn.techiop.hcert.kotlin.data.GreenCertificate

/**
 * Validates the HCERT data against the JSON schema.
 * Beware: By default [useFallback] is true, so we are trying to verify
 * the data against a very relaxed schema.
 */
class DefaultSchemaValidationService(private val useFallback: Boolean = true) : SchemaValidationService {

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

        if (useFallback) {
            val fallbackErrors = adapter.validateWithFallback()
            if (fallbackErrors.isNotEmpty()) throw VerificationException(
                Error.SCHEMA_VALIDATION_FAILED,
                "Data does not follow fallback schema: $fallbackErrors}"
            )
        } else {
            val errors = adapter.validateBasic(versionString)
            if (errors.isNotEmpty()) throw VerificationException(
                Error.SCHEMA_VALIDATION_FAILED,
                "Data does not follow fallback schema: $errors}"
            )
        }

        return adapter.toJson()
    }

}


