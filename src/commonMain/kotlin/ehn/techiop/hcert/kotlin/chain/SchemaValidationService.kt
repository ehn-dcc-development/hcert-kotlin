package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.data.GreenCertificate

interface SchemaValidationService {
    fun validate(data: GreenCertificate): Boolean
}