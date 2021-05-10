package ehn.techiop.hcert.kotlin.chain.faults

import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCwtService

/**
 * Encodes the input without the required CWT structure around it.
 *
 * **Should not be used in production.**
 */
class FaultyCwtService : DefaultCwtService() {

    override fun encode(input: ByteArray) = CBORObject.NewMap().also {
        it[CBORObject.FromObject(1)] = CBORObject.DecodeFromBytes(input)
    }.EncodeToBytes()

}