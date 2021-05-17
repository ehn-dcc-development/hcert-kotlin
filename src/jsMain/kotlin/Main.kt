import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.common.Base45Encoder
import ehn.techiop.hcert.kotlin.chain.fromHexString
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.crypto.Cbor
import ehn.techiop.hcert.kotlin.crypto.Cose
import ehn.techiop.hcert.kotlin.crypto.CoseJsEcPubKey
import ehn.techiop.hcert.kotlin.crypto.CurveIdentifier
import ehn.techiop.hcert.kotlin.data.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


fun main() {
    println(Base45Encoder.encode(ByteArray(5) { i -> i.toByte() }))
    val foo = "Foo"
    val bar = js("externalTest(foo)")

    val cert = GreenCertificate(
        "1.2", Person("Mustermann", null, "Max"), LocalDate(1980, 6, 8), listOf(
            Vaccination(
                ValueSetEntryAdapter("foo", ValueSetEntry("Voo", "DE", true, "Soylent", "ø")),
                ValueSetEntryAdapter("foo", ValueSetEntry("Voo", "DE", true, "Soylent", "ø")),
                ValueSetEntryAdapter("foo", ValueSetEntry("Voo", "DE", true, "Soylent", "ø")),
                ValueSetEntryAdapter("foo", ValueSetEntry("Voo", "DE", true, "Soylent", "ø")),
                9,
                100,
                LocalDate(2021, 8, 15),
                "AU",
                "Royal Navy",
                "HMCS"
            )
        ), null, null
    )
    println(Json.encodeToString(cert))
    val input = DefaultCborService().encode(cert)
    val encode = Base45Encoder.encode(input)
    println(encode)
    val compressed = DefaultCompressorService().encode(input)
    println(Base45Encoder.encode(compressed))
    val decompressed = DefaultCompressorService().decode(compressed, VerificationResult())

    println(input)
    println(decompressed)
    println("Match:" + input.contentEquals(decompressed))

    val ceert = DefaultCborService().decode(input, VerificationResult())
    println(Json.encodeToString(ceert))
    println(bar)

    val signedBitString =
        "d28443a10126a10442313172496d706f7274616e74206d6573736167652158404c2b6b66dfedc4cfef0f221cf7ac7f95087a4c4245fef0063a0fd4014b670f642d31e26d38345bb4efcdc7ded3083ab4fe71b62a23f766d83785f044b20534f9".fromHexString()
    val pubKey = CoseJsEcPubKey(
        "143329cce7868e416927599cf65a34f3ce2ffda55a7eca69ed8919a394d42f0f".fromHexString(),
        "60f7f1a780d8a783bfb7a2dd6b2796e8128dbbcef9d3d168db9529971a36e7b9".fromHexString(),
        CurveIdentifier.ED25519
    )
    Cose.verify(signedBitString, pubKey).then { println("Signature sucessfully verified!") }

    console.info(Cbor.decode(signedBitString))
    signedBitString[0] = 0
    Cose.verify(signedBitString, pubKey).catch { println("Could not verify with broken header: $it") }
}
