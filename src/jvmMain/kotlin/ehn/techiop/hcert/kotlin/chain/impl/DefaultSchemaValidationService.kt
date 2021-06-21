package ehn.techiop.hcert.kotlin.chain.impl

import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.SchemaValidationService
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import net.pwall.json.schema.JSONSchema
import net.pwall.json.schema.parser.Parser
import java.io.ByteArrayOutputStream
import java.net.URI

actual class DefaultSchemaValidationService : SchemaValidationService {

    private val schema: JSONSchema = loadSchema()

    override fun validate(cbor: ByteArray, verificationResult: VerificationResult) {
        val decoded = try {
            CBORObject.DecodeFromBytes(cbor)
        } catch (e: Throwable) {
            throw VerificationException(Error.CBOR_DESERIALIZATION_FAILED, e. message, e)
        }

        val json = ByteArrayOutputStream().let {
            decoded.WriteJSONTo(it)
            it.toString()
        }

        val result = schema.validateBasic(json)
        result.errors?.takeIf { it.isNotEmpty() }?.let { errors ->
            val errorString = errors.map { "Field ${it.instanceLocation}: ${it.error} (${it.keywordLocation})" }.joinToString("; ")
            throw VerificationException(Error.CBOR_DESERIALIZATION_FAILED, "Data does not follow schema: $errorString")
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
