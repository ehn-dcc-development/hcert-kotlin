package ehn.techiop.hcert.kotlin.chain.faults

import COSE.Sign1Message
import com.upokecenter.cbor.CBORObject
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService

/**
 * Encodes the input into a COSE structure with broken signature values
 *
 * **Should not be used in production.**
 */
class BrokenCoseService(private val cryptoService: CryptoService) : DefaultCoseService(cryptoService) {

    override fun encode(input: ByteArray): ByteArray {
        val signed = Sign1Message.DecodeFromBytes(super.encode(input)) as Sign1Message
        val cbor = signed.EncodeToCBORObject()
        cbor[3] = CBORObject.FromObject("foo")
        return cbor.EncodeToBytes()
    }

}