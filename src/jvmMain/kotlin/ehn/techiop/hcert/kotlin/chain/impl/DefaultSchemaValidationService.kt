package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.SchemaValidationService
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.impl.SchemaLoader.Companion.BASE_SCHEMA_VERSION
import ehn.techiop.hcert.kotlin.chain.impl.SchemaLoader.Companion.knownSchemaVersions
import ehn.techiop.hcert.kotlin.data.CborObject
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.trust.JvmCwtAdapter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.pwall.json.schema.JSONSchema
import net.pwall.json.schema.parser.Parser
import java.net.URI

class JvmSchemaLoader : SchemaLoader<JSONSchema>() {
    override fun loadSchema(version: String): JSONSchema = getSchemaResource(version).use { resource ->
        Parser(uriResolver = { resource }).parse(URI.create("dummy:///"))
    }

    private fun getSchemaResource(version: String) =
        DefaultSchemaValidationService::class.java.classLoader.getResourceAsStream("json/schema/$version/DCC.combined-schema.json")
            ?: throw IllegalArgumentException("Schema not found")
}

actual class DefaultSchemaValidationService : SchemaValidationService {
    private val schemaLoader = JvmSchemaLoader()
    override fun validate(cbor: CborObject, verificationResult: VerificationResult): GreenCertificate {
        try {
            val json = (cbor as JvmCwtAdapter.JvmCborObject).toJsonString()

            val versionString = cbor.getVersionString() ?: throw VerificationException(
                Error.CBOR_DESERIALIZATION_FAILED,
                "No schema version specified!"
            )
            val validator = schemaLoader.validators[versionString]
                ?: throw VerificationException(
                    Error.SCHEMA_VALIDATION_FAILED,
                    "Schema version $versionString is not supported. Supported versions are ${knownSchemaVersions.contentToString()}"
                )

            val result = validator.validateBasic(json)


            result.errors?.let { error ->
                if (error.isNotEmpty()) {
                    //fallback to 1.3.0, since certificates may only conform to this never schema, even though they declare otherwise
                    //this is OK, though, as long as the specified version is actually valid
                    if (versionString < "1.3.0") {
                        val validator13 = schemaLoader.validators[BASE_SCHEMA_VERSION]!!
                        val result13 = validator13.validateBasic(json)
                        result13.errors?.let { error13 ->
                            if (error13.isNotEmpty()) throw VerificationException(
                                Error.SCHEMA_VALIDATION_FAILED,
                                "Data does not follow schema 1.3.0: ${result13.errors?.map { "${it.error}: ${it.keywordLocation}, ${it.instanceLocation}" }}"
                            )
                            //TODO log warning
                        }
                    } else throw VerificationException(
                        Error.SCHEMA_VALIDATION_FAILED,
                        "Data does not follow schema $versionString: ${result.errors?.map { "${it.error}: ${it.keywordLocation}, ${it.instanceLocation}" }}"
                    )
                }
            }
            return Json { ignoreUnknownKeys = true }.decodeFromString(json)
        } catch (t: Throwable) {
            throw t
        }
    }

}
