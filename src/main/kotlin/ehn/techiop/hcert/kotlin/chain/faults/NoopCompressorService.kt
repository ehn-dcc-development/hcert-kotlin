package ehn.techiop.hcert.kotlin.chain.faults

import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import java.util.zip.Deflater
import java.util.zip.DeflaterInputStream

/**
 * Does not compress the input at all -- SPEC says input SHALL be compressed = required
 *
 * **Should not be used in production.**
 */
class NoopCompressorService : DefaultCompressorService() {

    override fun encode(input: ByteArray): ByteArray {
        return input
    }

}