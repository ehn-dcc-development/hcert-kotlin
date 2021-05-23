package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.SchemaValidationService
import ehn.techiop.hcert.kotlin.data.GreenCertificate

actual class DefaultSchemaValidationService : SchemaValidationService {
    override fun validate(data: GreenCertificate): Boolean {
        try{
            TODO("@ckollman: Implement JvmSchema validation ;-)")
        }catch (e:Throwable){
            e.printStackTrace()
        }
        return true
    }
}