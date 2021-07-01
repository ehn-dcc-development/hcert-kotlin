package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.impl.CompressionConstants.MAX_DECOMPRESSED_SIZE
import ehn.techiop.hcert.kotlin.chain.toHexString
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlin.random.Random

//Need to split this into two tests because "Spec styles that support nested tests are disabled in kotest-js because
//the underlying JS frameworks do not support promises for outer root scopes".

class CompressorServiceTest : StringSpec({
    "ZLib random compress+deflate" {
        val compressorService = DefaultCompressorService()
        val input = Random.nextBytes(32).toHexString()
        val deflated = compressorService.encode(input.encodeToByteArray())
        val encoded = Base45Encoder.encode(deflated)
        // transfer
        val decoded = Base45Encoder.decode(encoded)
        decoded.asList() shouldBe deflated.asList()
        val inflated = compressorService.decode(decoded, VerificationResult())
        inflated.decodeToString() shouldBe input
    }
})

class ChunkedCompressionTest : FunSpec({
    val rnd = Random(1337)
    val compressor = CompressorAdapter()
    withData(
        mapOf(
            "small" to rnd.nextBytes(674),
            //these next three make little sense, but hey…
            "larger" to rnd.nextBytes(3072),
            "~double chunk size" to rnd.nextBytes(2 * 65536),
            "MAX_DECOMPRESSED_SIZE" to rnd.nextBytes(MAX_DECOMPRESSED_SIZE),
            "too large" to rnd.nextBytes(1 + MAX_DECOMPRESSED_SIZE),
            "100MiB zLib bomb" to ByteArray(20 * MAX_DECOMPRESSED_SIZE)
        )
    ) { input ->
        val deflated = compressor.encode(input, 9)
        if (input.size > MAX_DECOMPRESSED_SIZE) {
            withClue("Exceeding MAX_DECOMPRESSED_SIZE") {
                shouldThrowExactly<IllegalArgumentException> {
                    compressor.decode(deflated)
                }
            }
        } else {
            val inflated = compressor.decode(deflated)
            inflated shouldBe input
        }
    }
})