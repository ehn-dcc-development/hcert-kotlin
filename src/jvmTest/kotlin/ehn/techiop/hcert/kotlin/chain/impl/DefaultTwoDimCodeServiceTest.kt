package ehn.techiop.hcert.kotlin.chain.impl

import com.google.zxing.BarcodeFormat
import ehn.techiop.hcert.kotlin.chain.asBase64
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.random.Random

class DefaultTwoDimCodeServiceTest : FunSpec({
    withData(
        TestInput(Random.nextBytes(32).asBase64(), BarcodeFormat.AZTEC, 300, 0),
        TestInput(Random.nextBytes(32).asBase64(), BarcodeFormat.AZTEC, 500, 2),
        TestInput(Random.nextBytes(32).asBase64(), BarcodeFormat.QR_CODE, 300, 4),
        TestInput(Random.nextBytes(32).asBase64(), BarcodeFormat.QR_CODE, 500, 8)
    ) { input ->
        val service = DefaultTwoDimCodeService(input.size, input.format)

        val encoded = service.encode(input.input)
        encoded shouldNotBe null
        encoded.asBase64().length shouldBeGreaterThan input.size

        val decoded = service.decode(encoded)
        decoded shouldBe input.input
    }
})

data class TestInput(val input: String, val format: BarcodeFormat, val size: Int, val marginSize: Int)