package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.common.Base45Encoder
import ehn.techiop.hcert.kotlin.chain.toHexString
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlin.random.Random


class Base45ServiceTest : FunSpec({

    withData(
        nameFn = { "String ${it.first} should be ${it.second}" },
        Pair("AB", "BB8"),
        Pair("Hello!!", "%69 VD92EX0"),
        Pair("base-45", "UJCLQE7W581"),
        Pair("ietf!", "QED8WEX0")
    ) { (plain, encoded) ->
        val implEncoded = Base45Encoder.encode(plain.encodeToByteArray())
        implEncoded shouldBe encoded
        Base45Encoder.decode(implEncoded).asList() shouldBe plain.encodeToByteArray().asList()
    }

    withData(
        nameFn = { "Bytes encode+decode $it" },
        (byteArrayOf(0)),
        (byteArrayOf(0, 15)),
        (byteArrayOf(0, 0)),
        (byteArrayOf(0, 0, 0))
    ) { deflated ->
        val encoded = Base45Encoder.encode(deflated)
        // transfer
        val decoded = Base45Encoder.decode(encoded)
        (decoded.asList() shouldBe deflated.asList())
    }


    test("ZLib random compress+deflate") {
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