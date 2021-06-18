package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.SchemaValidationService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.data.CborObject
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.trust.JvmCwtAdapter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.pwall.json.schema.JSONSchema
import net.pwall.json.schema.parser.Parser
import java.net.URI

actual class DefaultSchemaValidationService : SchemaValidationService {

    val schema12 = loadSchema("1.2.1")
    val schema13 = loadSchema("1.3.0")

    override fun validate(cbor: CborObject, verificationResult: VerificationResult): GreenCertificate {
        try {
            val json = (cbor as JvmCwtAdapter.JvmCborObject).toJsonString()
            val result = (if ("1.3.0" == cbor.getVersionString()) schema13 else schema12).validateBasic(json)
            result.errors?.let { error ->
                if (error.isNotEmpty()) {
                    throw Throwable("Data does not follow schema: ${result.errors?.map { "${it.error}: ${it.keywordLocation}, ${it.instanceLocation}" }}")
                }
            }
            return Json { ignoreUnknownKeys = true }.decodeFromString(json)
        } catch (t: Throwable) {
            throw t.also { verificationResult.error = Error.SCHEMA_VALIDATION_FAILED }
        }
    }

    companion object {
        private fun loadSchema(version: String): JSONSchema {
            val resource = javaClass.classLoader.getResourceAsStream("json/schema/$version/DCC.combined-schema.json")
                ?: throw IllegalArgumentException("Schema not found")
            val parser = Parser(uriResolver = { resource })
            return parser.parse(URI.create("dummy:///"))
        }
    }
}
