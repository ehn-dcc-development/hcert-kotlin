package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CompressorService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.toByteArray
import ehn.techiop.hcert.kotlin.chain.toUint8Array
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

actual open class DefaultCompressorService(private val compressionLevel: Int = 9) : CompressorService {
    override fun encode(input: ByteArray): ByteArray {
        return (Pako.deflate(input.toUint8Array(),
                object: Pako.DeflateFunctionOptions {
                    override var level: dynamic
                        get() = compressionLevel
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

    actual companion object {
        actual fun getInstance(): DefaultCompressorService {
            return DefaultCompressorService()
        }
    }
}