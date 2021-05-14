package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CompressorService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

actual open class DefaultCompressorService(private val compressionLevel: Int = 9) : CompressorService {
    private fun byteArrayToUint8Array(input: ByteArray): Uint8Array {
        return Uint8Array(input.toTypedArray())
    }

    private fun uint8ArrayToByteArray(input: Uint8Array): ByteArray {
        return ByteArray(input.length){ input[it] }
    }

    override fun encode(input: ByteArray): ByteArray {
        return uint8ArrayToByteArray(
            Pako.deflate(byteArrayToUint8Array(input),
                object: Pako.DeflateFunctionOptions {
                    override var level: dynamic
                        get() = compressionLevel
                        set(value) {}
                }) as Uint8Array
        )
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        verificationResult.zlibDecoded = false
        return try {
            return uint8ArrayToByteArray(Pako.inflate(byteArrayToUint8Array(input))).also {
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