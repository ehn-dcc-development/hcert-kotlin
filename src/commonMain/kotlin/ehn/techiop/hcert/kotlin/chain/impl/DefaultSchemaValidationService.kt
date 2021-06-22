package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.SchemaValidationService

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

expect class DefaultSchemaValidationService() : SchemaValidationService