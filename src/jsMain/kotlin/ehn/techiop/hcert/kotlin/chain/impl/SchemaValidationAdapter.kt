package ehn.techiop.hcert.kotlin.chain.impl

import AJV2020
import MainResourceHolder
import addFormats
import ehn.techiop.hcert.kotlin.data.CborObject
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.data.loadAsString
import ehn.techiop.hcert.kotlin.trust.JsCwtAdapter
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic

/**
 * Warning: AJV does not support the valueset-uri keyword used in the schema.
 * We configure AJV to ignore the keyword, but that still means we are not checking
 * field values against the allowed options from the linked value sets.
 */
internal class JsSchemaLoader : SchemaLoader<Pair<AJV2020, dynamic>>() {

    override fun loadSchema(version: String): Pair<AJV2020, dynamic> {
        loadAjv().apply {
            val schema: dynamic = getSchema(version)
            if (!this.validateSchema(schema))
                throw Throwable("JSON schema invalid: ${JSON.stringify(this.errors)}")
            return this to schema
        }
    }

    override fun loadFallbackSchema(): Pair<AJV2020, dynamic> {
        loadAjv().apply {
            val schema: dynamic = getFallbackSchema()
            if (!this.validateSchema(schema))
                throw Throwable("Relaxed JSON schema invalid: ${JSON.stringify(this.errors)}")
            return this to schema
        }
    }

    private fun getSchema(version: String): dynamic =
        JSON.parse(MainResourceHolder.loadAsString("json/schema/$version/DCC.combined-schema.json")!!)

    private fun getFallbackSchema(): dynamic =
        JSON.parse(MainResourceHolder.loadAsString("json/schema/fallback/DCC.combined-schema.json")!!)

    private fun loadAjv(): AJV2020 = AJV2020().apply {
        addFormats(this)
        addKeyword("valueset-uri")
    }

}

actual class SchemaValidationAdapter actual constructor(private val cbor: CborObject) {

    private val schemaLoader = JsSchemaLoader()

    //AJV operates directly on JS objects, if all properties check out, it validates nicely
    //we are working on the raw parsed CBOR structure for schema validation, which is actually the
    //closest we will ever get to direct cbor schema validation
    //however, CBOR tags and JSON schema do not go well together, so check if the only error thrown
    //concerns the tagged sc
    private val json = (cbor as JsCwtAdapter.JsCborObject).internalRepresentation

    actual fun hasValidator(versionString: String): Boolean {
        return schemaLoader.validators[versionString] != null
    }

    actual fun validateBasic(versionString: String): Collection<SchemaError> {
        val (ajv, schema) = schemaLoader.validators[versionString] ?: throw IllegalArgumentException("versionString")
        return validate(ajv, schema)
    }

    actual fun validateWithFallback(): Collection<SchemaError> {
        val (ajv, schema) = schemaLoader.loadFallbackSchema()
        return validate(ajv, schema)
    }

    private fun validate(ajv: AJV2020, schema: dynamic): List<SchemaError> {
        val tag = this::class.simpleName + hashCode().toString()
        Napier.v(tag = tag, message = "validating schema on object\n" + JSON.stringify(json, null, 2))
        val result = ajv.validate(schema, json)
        if (result) return listOf()
        return (ajv.errors as Array<dynamic>).map { SchemaError(JSON.stringify(it)) }
    }

    actual fun toJson(): GreenCertificate {
        return Json.decodeFromDynamic<GreenCertificate>(json)
    }

}
