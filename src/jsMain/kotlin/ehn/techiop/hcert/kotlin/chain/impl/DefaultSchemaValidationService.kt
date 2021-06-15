package ehn.techiop.hcert.kotlin.chain.impl

import AJV2020
import Cbor.DecoderOptions
import MainResourceHolder
import addFormats
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.SchemaValidationService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.catch
import ehn.techiop.hcert.kotlin.chain.jsTry
import ehn.techiop.hcert.kotlin.chain.toBuffer
import ehn.techiop.hcert.kotlin.chain.toHexString
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
        schema = JSON.parse<dynamic>(MainResourceHolder.loadAsString("json/DCC.combined-schema.json")!!)
        if (!ajv.validateSchema(schema)) {
            throw Throwable("JSON schema invalid: ${JSON.stringify(ajv.errors)}")
        }
    }


    override fun validate(cbor: ByteArray, verificationResult: VerificationResult) {
        jsTry {
            val json = Cbor.Decoder.decodeFirstSync(input = cbor.toBuffer(), options = object : DecoderOptions {})
            if (!ajv.validate(schema, json)) {
                val uppercase = cbor.toHexString().uppercase()
                if (uppercase.contains("627363C0") // optional cbor tag C1 for "sc"
                    && (ajv.errors as Array<*>).size == 1
                    && JSON.stringify(ajv.errors).contains("#/properties/sc/type")) {
                    // val sc = json.asDynamic()["t"][0]["sc"] // js("typeof sc") = object ... but why not a string?
                } else {
                    throw Throwable("Data does not follow schema: ${JSON.stringify(ajv.errors)}")
                }
            }
        }.catch {
            throw it.also {
                verificationResult.error = Error.SCHEMA_VALIDATION_FAILED
            }
        }
    }

}