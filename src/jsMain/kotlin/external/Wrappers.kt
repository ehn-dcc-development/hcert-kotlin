package Cbor

import Buffer
import ehn.techiop.hcert.kotlin.chain.toBuffer


object Wrappers {
    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    fun map() = Map(js("([])") as tsstdlib.Iterable<Any>)

    //it would be nice to do away with this, but the generated externals are somehow messed up
    @Suppress("UnsafeCastFromDynamic", "UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    fun dateDecoderOptions(): DecoderOptions = js("({tags:{0:function(x){return x}}})")

    //decodeFirstSync will cause issues in some edge cases, so we simply delegate to decodeAllSyncq
    fun decodeFirst(input: ByteArray): dynamic = decodeFirst(input.toBuffer())
    fun decodeFirst(input: Buffer): dynamic = decodeAll(input)[0]

    fun decodeAll(input: ByteArray) = decodeAll(input.toBuffer())
    fun decodeAll(input: Buffer) = Decoder.decodeAllSync(input, options = dateDecoderOptions())
}
