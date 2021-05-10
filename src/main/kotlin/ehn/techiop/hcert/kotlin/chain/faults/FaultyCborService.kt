package ehn.techiop.hcert.kotlin.chain.faults

import ehn.techiop.hcert.data.Eudgc
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService

/**
 * Encodes the input in a faulty CBOR encoding, i.e. reversed byte array
 *
 * **Should not be used in production.**
 */
class FaultyCborService : DefaultCborService() {

    override fun encode(input: Eudgc) = super.encode(input).reversed().toByteArray()

}