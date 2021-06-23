package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.SchemaValidationService
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.data.CborObject
import ehn.techiop.hcert.kotlin.data.GreenCertificate

//As of 1.3.0 our codebase handles all version equally well


//we need to work around Duplicate JVM class name bug → we can skip expect definitions altogether
abstract class SchemaLoader<T> {
    companion object {
        private const val BASE_VERSION = "1.3.0"
        private val KNOWN_VERSIONS = arrayOf(
            "1.0.0",
            "1.0.1",
            "1.1.0",
            "1.2.0",
            "1.2.1",
            "1.3.0"
        )
    }

    internal val validators = KNOWN_VERSIONS.mapIndexed { i, version ->
        KNOWN_VERSIONS[i] to loadSchema(version)
    }.toMap()

    internal val defaultValidator = validators[BASE_VERSION]!!

    abstract internal fun loadSchema(version: String): T

}

expect class SchemaValidationAdapter(cbor: CborObject) {

    fun hasValidator(versionString: String): Boolean
    fun validateBasic(versionString: String): Collection<SchemaError>
    fun toJson(): GreenCertificate
    fun validateWithFallback(): Collection<SchemaError>

}

data class SchemaError(val error: String)


class DefaultSchemaValidationService : SchemaValidationService {

    override fun validate(cbor: CborObject, verificationResult: VerificationResult): GreenCertificate {
        val adapter = SchemaValidationAdapter(cbor)

        val versionString = cbor.getVersionString() ?: throw VerificationException(
            Error.CBOR_DESERIALIZATION_FAILED,
            "No schema version specified!"
        )
        if (!adapter.hasValidator(versionString))
            throw VerificationException(
                Error.SCHEMA_VALIDATION_FAILED,
                "Schema version $versionString is not supported."
            )

        val errors = adapter.validateBasic(versionString)
        if (errors.isNotEmpty()) {
            if (versionString < "1.3.0") {
                validateWithFallback(adapter)
            } else throw VerificationException(
                Error.SCHEMA_VALIDATION_FAILED,
                "Data does not follow schema $versionString: $errors}"
            )
        }
        return adapter.toJson()
    }


    /**
     * fallback to 1.3.0, since certificates may only conform to this never schema, even though they declare otherwise
     * this is OK, though, as long as the specified version is actually valid
     */
    private fun validateWithFallback(adapter: SchemaValidationAdapter) {
        val errors = adapter.validateWithFallback()
        if (errors.isNotEmpty()) {
            throw VerificationException(
                Error.SCHEMA_VALIDATION_FAILED,
                "Data does not follow schema 1.3.0: $errors}"
                //TODO log warning
            )
        }
    }

}


