package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CompressorService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import pako.Inflate
import pako.deflate



actual open class DefaultCompressorService(private val level: Int = 9) : CompressorService {

    override fun encode(input: ByteArray): ByteArray {

        val deflate = deflate(Uint8Array(Uint8Array(input.toTypedArray())))

        return ByteArray(deflate.length){ deflate[it] }
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        // TODO: Compression Level, Verification Result
        return Inflate().inflate(input)
    }

    actual companion object {
        actual fun getInstance(): DefaultCompressorService {
            return DefaultCompressorService()
        }
    }
}