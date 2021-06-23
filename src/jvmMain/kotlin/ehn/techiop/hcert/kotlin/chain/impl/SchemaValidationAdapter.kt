package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.data.CborObject
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.trust.JvmCwtAdapter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.pwall.json.schema.JSONSchema
import net.pwall.json.schema.parser.Parser
import java.io.InputStream
import java.net.URI

class JvmSchemaLoader : SchemaLoader<JSONSchema>() {

    override fun loadSchema(version: String) = getSchemaResource(version).use(this@JvmSchemaLoader::parse)

    override fun loadFallbackSchema() = getFallbackSchema().use(this@JvmSchemaLoader::parse)

    private fun parse(resource: InputStream) = Parser(uriResolver = { resource }).parse(URI.create("dummy:///"))

    private fun getSchemaResource(version: String) =
        classLoader().getResourceAsStream("json/schema/$version/DCC.combined-schema.json")
            ?: throw IllegalArgumentException("Schema not found: $version")

    private fun getFallbackSchema() =
        classLoader().getResourceAsStream("json/schema/fallback/DCC.combined-schema.json")
            ?: throw IllegalArgumentException("Fallback schema not found")

    private fun classLoader() = SchemaValidationAdapter::class.java.classLoader

}

actual class SchemaValidationAdapter actual constructor(private val cbor: CborObject) {

    private val schemaLoader = JvmSchemaLoader()
    private val json = (cbor as JvmCwtAdapter.JvmCborObject).toJsonString()

    actual fun hasValidator(versionString: String): Boolean {
        return schemaLoader.validators[versionString] != null
    }

    actual fun validateBasic(versionString: String): Collection<SchemaError> {
        val validator = schemaLoader.validators[versionString] ?: throw IllegalArgumentException("versionString")
        return validate(validator)
    }

    actual fun validateWithFallback(): Collection<SchemaError> {
        val validator = schemaLoader.loadFallbackSchema()
        return validate(validator)
    }

    private fun validate(validator: JSONSchema): Collection<SchemaError> {
        val result = validator.validateBasic(json)
        return result.errors?.map { SchemaError("${it.error}, ${it.keywordLocation}, ${it.instanceLocation}") }
            ?: listOf()
    }

    actual fun toJson(): GreenCertificate {
        return Json.decodeFromString(json)
    }

}
