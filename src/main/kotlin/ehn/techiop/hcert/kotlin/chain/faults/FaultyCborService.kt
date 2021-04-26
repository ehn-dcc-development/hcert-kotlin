package ehn.techiop.hcert.kotlin.chain.faults

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.data.Eudgc
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService


/**
 * Encodes the input without the required structure around it.
 *
 * **Should not be used in production.**
 */
class FaultyCborService : DefaultCborService() {

    override fun encode(input: Eudgc): ByteArray {
        val cbor = CBORMapper().writeValueAsBytes(input)
        return CBORObject.FromObject(cbor).EncodeToBytes()
    }

}