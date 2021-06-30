package ehn.techiop.hcert.kotlin.chain.impl

import Buffer
import ehn.techiop.hcert.kotlin.chain.toBuffer

object CborHelper {
    //it would be nice to do away with this, but the generated externals are somehow messed up
    private val dateDecoderOptions = js("({tags:{0:function(x){return x}}})")

    //decodeFirstSync will cause issues in some edge cases, so we simply delegate to decodeAllSyncq
    fun decodeFirst(input: ByteArray): dynamic = decodeFirst(input.toBuffer())
    fun decodeFirst(input: Buffer): dynamic = decodeAll(input)[0].asDynamic()

    fun decodeAll(input: ByteArray) = decodeAll(input.toBuffer())
    fun decodeAll(input: Buffer) = Cbor.Decoder.decodeAllSync(input, options = dateDecoderOptions)
}