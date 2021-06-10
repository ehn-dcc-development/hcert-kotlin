package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.catch
import ehn.techiop.hcert.kotlin.chain.jsTry
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array
import org.khronos.webgl.Uint8Array

actual class CompressorAdapter {

    actual fun encode(input: ByteArray, level: Int) = (Pako.deflate(input.toUint8Array(),
        object : Pako.DeflateFunctionOptions {
            override var level: dynamic
                get() = level
                set(@Suppress("UNUSED_PARAMETER") value) {}
        }) as Uint8Array).toByteArray()

    actual fun decode(input: ByteArray) =
        jsTry { Pako.inflate(input.toUint8Array()).toByteArray() }.catch { throw it }

}