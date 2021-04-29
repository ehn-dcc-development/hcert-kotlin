package ehn.techiop.hcert.kotlin.chain.impl

import com.google.zxing.BarcodeFormat
import ehn.techiop.hcert.kotlin.chain.asBase64
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.random.Random

class DefaultTwoDimCodeServiceTest {

    @ParameterizedTest
    @MethodSource("inputProvider")
    fun randomEncodeDecode(input: TestInput) {
        val service = DefaultTwoDimCodeService(input.size, input.format)

        val encoded = service.encode(input.input)
        assertThat(encoded, notNullValue())
        assertThat(encoded.asBase64().length, greaterThan(input.size))

        val decoded = service.decode(encoded)
        assertThat(decoded, equalTo(input.input))
    }

    companion object {

        @JvmStatic
        @Suppress("unused")
        fun inputProvider() = listOf(
            TestInput(Random.nextBytes(32).asBase64(), BarcodeFormat.AZTEC, 300, 0),
            TestInput(Random.nextBytes(32).asBase64(), BarcodeFormat.AZTEC, 500, 2),
            TestInput(Random.nextBytes(32).asBase64(), BarcodeFormat.QR_CODE, 300, 4),
            TestInput(Random.nextBytes(32).asBase64(), BarcodeFormat.QR_CODE, 500, 8),
        )

    }

    data class TestInput(val input: String, val format: BarcodeFormat, val size: Int, val marginSize: Int)

}