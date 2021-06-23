package ehn.techiop.hcert.kotlin.chain.impl

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

    internal abstract fun loadSchema(version: String): T

    internal abstract fun loadFallbackSchema(): T

}

expect class SchemaValidationAdapter(cbor: CborObject) {

    fun hasValidator(versionString: String): Boolean
    fun validateBasic(versionString: String): Collection<SchemaError>
    fun toJson(): GreenCertificate
    fun validateWithFallback(): Collection<SchemaError>

}

data class SchemaError(val error: String)

