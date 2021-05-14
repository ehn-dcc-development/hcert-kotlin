@file:JsModule("pako")
package pako

import org.khronos.webgl.Uint8Array


external class Deflate {
    fun deflate(data: ByteArray): ByteArray
}


external class Inflate {
    fun inflate(data: ByteArray): ByteArray
}

external fun deflate(input: Uint8Array): Uint8Array

