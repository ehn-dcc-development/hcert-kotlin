package ehn.techiop.hcert.kotlin.chain.impl

import AJV2020
import addFormats
import ehn.techiop.hcert.kotlin.chain.SchemaValidationService
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.data.loadAsString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual class DefaultSchemaValidationService : SchemaValidationService {
    override fun validate(data: GreenCertificate): Boolean {
        val json = JSON.parse<dynamic>(Json.encodeToString(data))
        val ajv = AJV2020()
        addFormats(ajv)

        // Warning: AJV does not support the valueset-uri keyword used in the schema.
        // We configure AJV to ignore the keyword, but that still means we are not checking
        // field values against the allowed options from the linked value sets.
        ajv.addKeyword("valueset-uri")

        val schemaString = MainResourceHolder.loadAsString("json/DGC.combined-schema.json")
        val schema = JSON.parse<dynamic>(schemaString!!)

        val schemaValid = ajv.validateSchema(schema)
        if (!schemaValid) {
            console.log("Schema invalid:")
            console.log(JSON.stringify(ajv.errors))
        }

        val valid = ajv.validate(schema, json)
        if (!valid) {
            console.log("Data does not follow schema:")
            console.log(JSON.stringify(ajv.errors))
        }
        return valid
    }
}