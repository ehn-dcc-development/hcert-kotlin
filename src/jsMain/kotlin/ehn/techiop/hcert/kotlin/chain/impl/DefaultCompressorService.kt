package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CompressorService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array
import org.khronos.webgl.Uint8Array

actual open class DefaultCompressorService actual constructor(private val level: Int) : CompressorService {
    override fun encode(input: ByteArray): ByteArray {
        return (Pako.deflate(input.toUint8Array(),
            object : Pako.DeflateFunctionOptions {
                override var level: dynamic
                    get() = this@DefaultCompressorService.level
                    set(value) {}
            }) as Uint8Array).toByteArray()
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.zlibDecoded = false
        return try {
            return Pako.inflate(input.toUint8Array()).toByteArray().also {
                verificationResult.zlibDecoded = true
            }
        } catch (e: Throwable) {
            input
        }
    }

}