package ehn.techiop.hcert.kotlin.chain

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.*

class Base45ServiceTest {

    private val base45Service = Base45Encoder()

    @ParameterizedTest
    @MethodSource("stringProvider")
    fun testString(input: TestInput) {
        val enc = base45Service.encode(input.plain.toByteArray())
        assertEquals(input.encoded, enc)
        assertArrayEquals(input.plain.toByteArray(), base45Service.decode(enc))
    }

    @ParameterizedTest
    @MethodSource("bytesProvider")
    fun testBytes(input: TestInputBytes) {
        val deflated = input.bytes
        val encoded = base45Service.encode(deflated)
        // transfer
        val decoded = base45Service.decode(encoded)
        assertArrayEquals(deflated, decoded)
    }

    @Test
    fun testWithZlib() {
        val compressorService = CompressorService()
        val input = UUID.randomUUID().toString()
        val deflated = compressorService.encode(input.encodeToByteArray())
        val encoded = base45Service.encode(deflated)
        // transfer
        val decoded = base45Service.decode(encoded)
        assertArrayEquals(deflated, decoded)
        val inflated = compressorService.decode(decoded, VerificationResult())
        assertEquals(input, String(inflated))
    }

    companion object {

        // from RFC draft
        @JvmStatic
        @Suppress("unused")
        fun stringProvider() = listOf(
            TestInput("AB", "BB8"),
            TestInput("Hello!!", "%69 VD92EX0"),
            TestInput("base-45", "UJCLQE7W581"),
            TestInput("ietf!", "QED8WEX0"),
        )

        // edge cases
        @JvmStatic
        @Suppress("unused")
        fun bytesProvider() = listOf(
            TestInputBytes(byteArrayOf(0)),
            TestInputBytes(byteArrayOf(0, 15)),
            TestInputBytes(byteArrayOf(0, 0)),
            TestInputBytes(byteArrayOf(0, 0, 0)),
        )
    }

    data class TestInput(val plain: String, val encoded: String)

    data class TestInputBytes(val bytes: ByteArray)

}
