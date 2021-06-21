package ehn.techiop.hcert.kotlin.chain.impl

import AJV2020
import Cbor.DecodeOptions
import MainResourceHolder
import addFormats
import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.chain.catch
import ehn.techiop.hcert.kotlin.chain.jsTry
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
        val json = jsTry {
            Cbor.Decoder.decodeFirstSync(input = cbor.toBuffer(), options = object : DecodeOptions {})
        }.catch {
            throw VerificationException(Error.CBOR_DESERIALIZATION_FAILED, it.message, it)
        }

        if (!ajv.validate(schema, json))
            throw VerificationException(Error.CBOR_DESERIALIZATION_FAILED, "Data does not follow schema: ${JSON.stringify(ajv.errors)}")
        // console.log(JSON.stringify(ajv.errors))
    }
}
