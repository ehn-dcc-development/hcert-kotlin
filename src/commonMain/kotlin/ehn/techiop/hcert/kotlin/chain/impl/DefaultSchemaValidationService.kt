package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.SchemaValidationService
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.data.CborObject
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import kotlin.jvm.JvmOverloads

/**
 * Validates the HCERT data against the JSON schema.
 * Beware: By default [useFallback] is true, so we are trying to verify
 * the data against a very relaxed schema.
 */
class DefaultSchemaValidationService @JvmOverloads constructor(private val useFallback: Boolean = true) :
    SchemaValidationService {

    override fun validate(cbor: CborObject, verificationResult: VerificationResult): GreenCertificate {
        val adapter = SchemaValidationAdapter(cbor)

        val versionString = cbor.getVersionString() ?: throw VerificationException(
            Error.CBOR_DESERIALIZATION_FAILED,
            "No schema version specified",
            details = mapOf("schemaVersion" to "null")
        )
        if (!adapter.hasValidator(versionString)) throw VerificationException(
            Error.SCHEMA_VALIDATION_FAILED,
            "Schema version $versionString is not supported",
            details = mapOf("schemaVersion" to versionString)
        )

        if (useFallback) {
            val fallbackErrors = adapter.validateWithFallback()
            if (fallbackErrors.isNotEmpty()) throw VerificationException(
                Error.SCHEMA_VALIDATION_FAILED,
                "Data does not follow fallback schema: $fallbackErrors}",
                details = mapOf("schemaErrors" to fallbackErrors.joinToString())
            )
        } else {
            val errors = adapter.validateBasic(versionString)
            if (errors.isNotEmpty()) throw VerificationException(
                Error.SCHEMA_VALIDATION_FAILED,
                "Data does not follow fallback schema: $errors}",
                details = mapOf("schemaErrors" to errors.joinToString())
            )
        }

        return adapter.toJson()
    }

}


