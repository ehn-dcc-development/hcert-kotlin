package ehn.techiop.hcert.kotlin.chain.faults

import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.data.GreenCertificate

/**
 * Encodes the input in a faulty CBOR encoding, i.e. reversed byte array
 *
 * **Should not be used in production.**
 */
class FaultyCborService : DefaultCborService() {

    override fun encode(input: GreenCertificate) = super.encode(input).reversed().toByteArray()

}
