package ehn.techiop.hcert.kotlin.chain.impl

actual class DefaultSchemaValidationService : SchemaValidationService {
    override fun validate(data: GreenCertificate): Boolean {
        throw UnsupportedOperationException("TODO: Not implemented yet")
    }
}