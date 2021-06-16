package ehn.techiop.hcert.kotlin.chain.impl

import AJV2020
import MainResourceHolder
import addFormats
import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.data.CborObject
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.data.loadAsString
import ehn.techiop.hcert.kotlin.trust.JsCwtAdapter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic


actual class DefaultSchemaValidationService : SchemaValidationService {
    val ajv = AJV2020()
    val schema: dynamic

    init {
        addFormats(ajv)
        // Warning: AJV does not support the valueset-uri keyword used in the schema.
        // We configure AJV to ignore the keyword, but that still means we are not checking
        // field values against the allowed options from the linked value sets.
        ajv.addKeyword("valueset-uri")
        schema = JSON.parse(MainResourceHolder.loadAsString("json/DCC.combined-schema.json")!!)
        if (!ajv.validateSchema(schema)) {
            throw Throwable("JSON schema invalid: ${JSON.stringify(ajv.errors)}")
        }
    }


    override fun validate(cbor: CborObject, verificationResult: VerificationResult): GreenCertificate {
        return jsTry {
            //AJV operates directly on JS objects, if all properties check out, it validates nicely
            //we are working on the raw parsed CBOR structure for schema validation, which is actually the
            //closest we will ever get to direct cbor schema validation
            //however, CBOR tags and JSON schema do not go well together, so check if the only error thrown
            //concerns the tagged sc
            val json = (cbor as JsCwtAdapter.JsCborObject).internalRepresentation
            if (!ajv.validate(schema, json))
                throw Throwable("Stripped data also does not follow schema: ${JSON.stringify(ajv.errors)}")

            Json { ignoreUnknownKeys = true }.decodeFromDynamic<GreenCertificate>(json)
        }.catch {
            throw it.also {
                verificationResult.error = Error.SCHEMA_VALIDATION_FAILED
            }
        }
    }
}