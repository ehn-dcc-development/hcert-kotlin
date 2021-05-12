package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CompressorService
import ehn.techiop.hcert.kotlin.chain.VerificationResult

@JsModule("pako")
@JsNonModule
external class Deflater {
    fun deflate(data: ByteArray): ByteArray
}

@JsModule("pako")
@JsNonModule
external class Inflater {
    fun inflate(data: ByteArray): ByteArray
}

actual open class DefaultCompressorService(private val level: Int = 9) : CompressorService {
    override fun encode(input: ByteArray): ByteArray {
        // TODO: Compression Level
        return Deflater().deflate(input)
    }

    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray {
        // TODO: Compression Level, Verification Result
        return Inflater().inflate(input)
    }

    actual companion object {
        actual fun getInstance(): DefaultCompressorService {
            return DefaultCompressorService()
        }
    }
}