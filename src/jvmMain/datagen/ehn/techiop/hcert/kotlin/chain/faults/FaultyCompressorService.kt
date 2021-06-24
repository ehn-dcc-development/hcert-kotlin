package ehn.techiop.hcert.kotlin.chain.faults

import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import java.util.zip.Deflater
import java.util.zip.DeflaterInputStream

/**
 * Reverses the ZLIB encoding, resulting in a non-decodable output.
 *
 * **Should not be used in production.**
 */
class FaultyCompressorService : DefaultCompressorService() {

    override fun encode(input: ByteArray): ByteArray {
        return DeflaterInputStream(input.inputStream(), Deflater(9)).readBytes().reversedArray()
    }

}