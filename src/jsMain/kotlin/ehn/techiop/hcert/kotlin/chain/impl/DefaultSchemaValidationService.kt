package ehn.techiop.hcert.kotlin.chain.impl

import AJV2020
import Cbor.DecodeOptions
import MainResourceHolder
import addFormats
import ehn.techiop.hcert.kotlin.chain.SchemaValidationService
import ehn.techiop.hcert.kotlin.chain.catch
import ehn.techiop.hcert.kotlin.chain.jsTry
import ehn.techiop.hcert.kotlin.chain.toBuffer
import ehn.techiop.hcert.kotlin.data.loadAsString

actual class DefaultSchemaValidationService : SchemaValidationService {
    val ajv = AJV2020()
    val schema: dynamic

    init {
        addFormats(ajv)

        // Warning: AJV does not support the valueset-uri keyword used in the schema.
        // We configure AJV to ignore the keyword, but that still means we are not checking
        // field values against the allowed options from the linked value sets.
        ajv.addKeyword("valueset-uri")

        val schemaString = MainResourceHolder.loadAsString("json/DGC.combined-schema.json")
        schema = JSON.parse<dynamic>(schemaString!!)

        if (!ajv.validateSchema(schema)) {
            throw Throwable("JSON schema invalid: ${JSON.stringify(ajv.errors)}")
        }
    }


    override fun validate(cbor: ByteArray): Boolean {
        return jsTry {
            val json = Cbor.Decoder.decodeFirstSync(input = cbor.toBuffer(), options = object : DecodeOptions {})

            ajv.validate(schema, json)/*.also {
                if (!it) {
                    console.log("Data does not follow schema:")
                    console.log(JSON.stringify(ajv.errors))
                }
            }*/

        }.catch { false }

    }
}