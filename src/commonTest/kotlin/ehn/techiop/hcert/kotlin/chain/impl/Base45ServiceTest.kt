package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.common.Base45Encoder
import ehn.techiop.hcert.kotlin.chain.toHexString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalUnsignedTypes::class)
class Base45ServiceTest {

    @Test
    fun testString() {
        testString("AB", "BB8")
        testString("Hello!!", "%69 VD92EX0")
        testString("base-45", "UJCLQE7W581")
        testString("ietf!", "QED8WEX0")
    }

    private fun testString(plain: String, encoded: String) {
        assertEquals(Base45Encoder.encode(plain.encodeToByteArray()), encoded)
        assertEquals(Base45Encoder.decode(Base45Encoder.encode(plain.encodeToByteArray())), plain.encodeToByteArray())
    }

    @Test
    fun testBytes() {
        testBytes(byteArrayOf(0))
        testBytes(byteArrayOf(0, 15))
        testBytes(byteArrayOf(0, 0))
        testBytes(byteArrayOf(0, 0, 0))
    }

    private fun testBytes(deflated: ByteArray) {
        val encoded = Base45Encoder.encode(deflated)
        // transfer
        val decoded = Base45Encoder.decode(encoded)
        assertEquals(decoded, deflated)
    }

    @Test
    fun testWithZlib() {
        val compressorService = DefaultCompressorService()
        val input = Random.nextBytes(32).toHexString()
        val deflated = compressorService.encode(input.encodeToByteArray())
        val encoded = Base45Encoder.encode(deflated)
        // transfer
        val decoded = Base45Encoder.decode(encoded)
        assertEquals(decoded, deflated)
        val inflated = compressorService.decode(decoded, VerificationResult())
        assertEquals(inflated.decodeToString(), input)
    }

}