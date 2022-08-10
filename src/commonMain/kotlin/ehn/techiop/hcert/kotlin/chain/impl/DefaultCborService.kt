package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CborService
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray

open class DefaultCborService : CborService {

    override fun encode(input: GreenCertificate) = Cbor.encodeToByteArray(input)

}
