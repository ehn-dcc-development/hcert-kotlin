package ehn.techiop.hcert.kotlin.chain.impl

import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.SchemaValidationService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import net.pwall.json.schema.JSONSchema
import java.io.ByteArrayOutputStream

actual class DefaultSchemaValidationService : SchemaValidationService {

    override fun validate(cbor: ByteArray, verificationResult: VerificationResult) {
        try {
            val decoded = CBORObject.DecodeFromBytes(cbor)
            val json = ByteArrayOutputStream().let {
                decoded.WriteJSONTo(it)
                it.toString()
            }
            val resource = javaClass.classLoader.getResource("json/DCC.combined-schema.json")
                ?: throw IllegalArgumentException("Schema not found")
            val schema = JSONSchema.parser.parse(resource.toURI())
            val result = schema.validateBasic(json)
            result.errors?.let {
                if (it.isNotEmpty()) {
                    //it.forEach { println("${it.error} - ${it.instanceLocation}") }
                    throw Throwable("Data does not follow schema: ${result.errors}")
                }
            }
        } catch (t: Throwable) {
            verificationResult.error = Error.CBOR_DESERIALIZATION_FAILED
        }
    }

}