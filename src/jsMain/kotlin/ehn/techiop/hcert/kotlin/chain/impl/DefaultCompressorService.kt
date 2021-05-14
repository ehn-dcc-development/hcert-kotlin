package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CompressorService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

actual open class DefaultCompressorService(private val level: Int = 9) : CompressorService {
    private fun byteArrayToUint8Array(input: ByteArray): Uint8Array {
        return Uint8Array(input.toTypedArray())
    }

    private fun uint8ArrayToByteArray(input: Uint8Array): ByteArray {
        return ByteArray(input.length){ input[it] }
    }

    override fun encode(input: ByteArray): ByteArray {
        // TODO: Compression Level
        return uint8ArrayToByteArray(Pako.deflate(byteArrayToUint8Array(input)))
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        // TODO: Compression Level, Verification Result
        return uint8ArrayToByteArray(Pako.inflate(byteArrayToUint8Array(input)))
    }

    actual companion object {
        actual fun getInstance(): DefaultCompressorService {
            return DefaultCompressorService()
        }
    }
}