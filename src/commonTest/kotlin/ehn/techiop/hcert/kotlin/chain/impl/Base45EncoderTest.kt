package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.common.Base45Encoder
import ehn.techiop.hcert.kotlin.chain.fromHexString
import ehn.techiop.hcert.kotlin.chain.toHexString
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe


class Base45EncoderTest : FunSpec({

    withData(
        nameFn = { "Encode String \"${it.first}\" to \"${it.second}\"" },
        Pair("AB", "BB8"),
        Pair("Hello!!", "%69 VD92EX0"),
        Pair("base-45", "UJCLQE7W581"),
        Pair("ietf!", "QED8WEX0")
    ) { (plain, encoded) ->
        val input = plain.encodeToByteArray()
        val implEncoded = Base45Encoder.encode(input)
        implEncoded shouldBe encoded
        val implDecoded = Base45Encoder.decode(implEncoded)
        implDecoded.asList() shouldBe input.asList()
    }

    withData(
        nameFn = { "Encode Bytes (hex) \"${it.first.toHexString()}\" to \"${it.second}\"" },
        Pair("00".fromHexString(), "00"),
        Pair("07".fromHexString(), "70"),
        Pair("ff".fromHexString(), "U5"),
        Pair("000f".fromHexString(), "F00"),
        Pair("0000".fromHexString(), "000"),
        Pair("ffff".fromHexString(), "FGW"),
        Pair("000000".fromHexString(), "00000"),
    ) { (input, encoded) ->
        val implEncoded = Base45Encoder.encode(input)
        implEncoded shouldBe encoded
        val implDecoded = Base45Encoder.decode(implEncoded)
        implDecoded.asList() shouldBe input.asList()
    }

    withData(
        nameFn = { "Decode \"${it}\" is not valid" },
        "V5", // first value with 2 chars too large, after "U5"
        "AA", // too large
        "GGW", // first value with 3 chars too large, after "FGW"
        "WWW", // too large
        "7", // one char strings are not allowed
        "0" // one char strings are not allowed
    ) { encoded ->
        shouldThrow<IllegalArgumentException> {
            println(Base45Encoder.decode(encoded).toHexString())
        }
    }

})