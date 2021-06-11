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
            val json = ByteArrayOutputStream().let { decoded.WriteJSONTo(it);it.toString() }

            val schema =
                JSONSchema.parser.parse(javaClass.classLoader.getResource("json/DCC.combined-schema.json").toURI())

            val output = schema.validateBasic(json)

            output.errors?.forEach {
                println("${it.error} - ${it.instanceLocation}")
                verificationResult.error = Error.CBOR_DESERIALIZATION_FAILED
            }
        } catch (t: Throwable) {
            verificationResult.error = Error.CBOR_DESERIALIZATION_FAILED
        }
    }

}