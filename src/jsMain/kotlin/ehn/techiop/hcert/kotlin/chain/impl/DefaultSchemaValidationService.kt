package ehn.techiop.hcert.kotlin.chain.impl

import AJV2020
import MainResourceHolder
import addFormats
import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.chain.NonNullableTryCatch.catch
import ehn.techiop.hcert.kotlin.chain.NonNullableTryCatch.jsTry
import ehn.techiop.hcert.kotlin.chain.impl.SchemaLoader.Companion.BASE_SCHEMA_VERSION
import ehn.techiop.hcert.kotlin.chain.impl.SchemaLoader.Companion.knownSchemaVersions
import ehn.techiop.hcert.kotlin.data.CborObject
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.data.loadAsString
import ehn.techiop.hcert.kotlin.trust.JsCwtAdapter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic

internal class JsSchemaLoader : SchemaLoader<Pair<AJV2020, dynamic>>() {
    override fun loadSchema(version: String): Pair<AJV2020, dynamic> {
        //TODO load multiple schema versions into single instance and access by name
        val ajV2020 = AJV2020()
        addFormats(ajV2020)
        // Warning: AJV does not support the valueset-uri keyword used in the schema.
        // We configure AJV to ignore the keyword, but that still means we are not checking
        // field values against the allowed options from the linked value sets.
        ajV2020.addKeyword("valueset-uri")
        val schema: dynamic =
            JSON.parse(MainResourceHolder.loadAsString("json/schema/$version/DCC.combined-schema.json")!!)
        if (!ajV2020.validateSchema(schema)) {
            throw Throwable("JSON schema invalid: ${JSON.stringify(ajV2020.errors)}")
        }
        return ajV2020 to schema
    }
}

actual class DefaultSchemaValidationService : SchemaValidationService {
    private val schemaLoader = JsSchemaLoader()
    override fun validate(cbor: CborObject, verificationResult: VerificationResult): GreenCertificate {
        return jsTry {
            //AJV operates directly on JS objects, if all properties check out, it validates nicely
            //we are working on the raw parsed CBOR structure for schema validation, which is actually the
            //closest we will ever get to direct cbor schema validation
            //however, CBOR tags and JSON schema do not go well together, so check if the only error thrown
            //concerns the tagged sc
            val json = (cbor as JsCwtAdapter.JsCborObject).internalRepresentation
            val versionString = cbor.getVersionString() ?: throw VerificationException(Error.SCHEMA_VALIDATION_FAILED,"No schema version specified!")
            val (ajv, schema) = schemaLoader.validators[versionString]
                ?: throw VerificationException(Error.SCHEMA_VALIDATION_FAILED, "Schema version $versionString is not supported. Supported versions are ${knownSchemaVersions.contentToString()}")

            if (!ajv.validate(schema, json)) {
                //fallback to 1.3.0, since certificates may only conform to this never schema, even though they declare otherwise
                //this is OK, though, as long as the specified version is actually valid
                if (versionString < "1.3.0") {
                    val (ajv13, schema13) = schemaLoader.validators[BASE_SCHEMA_VERSION]!!
                    if (!ajv13.validate(schema13, json))
                        throw VerificationException(Error.SCHEMA_VALIDATION_FAILED, "Stripped data also does not follow schema 1.3.0: ${JSON.stringify(ajv13.errors)}")
                    //TODO log warning
                } else throw VerificationException(Error.SCHEMA_VALIDATION_FAILED, "Stripped data also does not follow schema $versionString: ${JSON.stringify(ajv.errors)}")
            }
            Json { ignoreUnknownKeys = true }.decodeFromDynamic<GreenCertificate>(json)
        }.catch {
            throw it
        }
    }
}
