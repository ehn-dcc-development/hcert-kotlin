package ehn.techiop.hcert.kotlin.chain.faults

import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import java.util.zip.Deflater
import java.util.zip.DeflaterInputStream

class FaultyCompressorService : DefaultCompressorService() {

    override fun encode(input: ByteArray): ByteArray {
        return DeflaterInputStream(input.inputStream(), Deflater(9)).readBytes().also {
            it.plus(byteArrayOf(0x00, 0x01, 0x02, 0x03))
        }
    }

}