package ehn.techiop.hcert.kotlin.chain.impl

import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.SchemaValidationService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import net.pwall.json.schema.JSONSchema
import net.pwall.json.schema.parser.Parser
import java.io.ByteArrayOutputStream
import java.net.URI

actual class DefaultSchemaValidationService : SchemaValidationService {

    private val schema: JSONSchema = loadSchema()

    override fun validate(cbor: ByteArray, verificationResult: VerificationResult) {
        try {
            val decoded = CBORObject.DecodeFromBytes(cbor)
            val json = ByteArrayOutputStream().let {
                decoded.WriteJSONTo(it)
                it.toString()
            }
            val result = schema.validateBasic(json)
            result.errors?.let { error ->
                if (error.isNotEmpty()) {
                    throw Throwable("Data does not follow schema: ${result.errors?.map { "${it.error}: ${it.keywordLocation}, ${it.instanceLocation}" }}")
                }
            }
        } catch (t: Throwable) {
            throw t.also { verificationResult.error = Error.CBOR_DESERIALIZATION_FAILED }
        }
    }

    private fun loadSchema(): JSONSchema =
        getSchemaResource().use { resource ->
            Parser(uriResolver = { resource }).parse(URI.create("dummy:///"))
        }

    private fun getSchemaResource() =
        javaClass.classLoader.getResourceAsStream("json/DCC.combined-schema.json")
            ?: throw IllegalStateException("Schema not found")
}
