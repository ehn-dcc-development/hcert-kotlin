package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.asBase64
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldNotBe
import kotlin.random.Random

class DefaultTwoDimCodeServiceJsTest : FunSpec({
    withData(
        TestInput(Random.nextBytes(32).asBase64(), 1, 0),
        TestInput(Random.nextBytes(32).asBase64(), 2, 2),
        TestInput(Random.nextBytes(32).asBase64(), 3, 4),
        TestInput(Random.nextBytes(32).asBase64(), 4, 8)
    ) { input ->
        val service = DefaultTwoDimCodeService(input.moduleSize)

        val encoded = service.encode(input.input)
        encoded shouldNotBe null
        encoded.asBase64().length shouldBeGreaterThan (input.moduleSize * input.moduleSize)
        // decoding the image from encode isn't supported by the JS library
        //val decoded = service.decode(encoded)
        //decoded shouldBe input.input
    }
})

data class TestInput(val input: String, val moduleSize: Int, val marginSize: Int)
