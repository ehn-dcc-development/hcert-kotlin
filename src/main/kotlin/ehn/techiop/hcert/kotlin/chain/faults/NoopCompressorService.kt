package ehn.techiop.hcert.kotlin.chain.faults

import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import java.util.zip.Deflater
import java.util.zip.DeflaterInputStream

/**
 * Does not compress the input at all -- should not impact validation
 */
class NoopCompressorService : DefaultCompressorService() {

    override fun encode(input: ByteArray): ByteArray {
        return input
    }

}