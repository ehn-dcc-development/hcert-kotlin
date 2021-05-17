import Asn1js.fromBER
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.common.Base45Encoder
import ehn.techiop.hcert.kotlin.chain.fromBase64
import ehn.techiop.hcert.kotlin.chain.fromBase64Url
import ehn.techiop.hcert.kotlin.chain.fromHexString
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.crypto.Buffer
import ehn.techiop.hcert.kotlin.crypto.Cbor
import ehn.techiop.hcert.kotlin.crypto.Cose
import ehn.techiop.hcert.kotlin.crypto.CoseJsEcPubKey
import ehn.techiop.hcert.kotlin.crypto.CurveIdentifier
import ehn.techiop.hcert.kotlin.data.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import pkijs.src.Certificate.Certificate
import tsstdlib.Uint8ArrayConstructor


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

    // from https://dgc.a-sit.at/ehn/testsuite
    val signedBitString =
        "d2844da20448d919375fc1e7b6b20126a0590124a4041a60a4d36d061a60a2306d01624154390103a101a4617681aa62646e01626d616d4f52472d3130303033303231356276706a313131393330353030356264746a323032312d30322d313862636f624154626369783075726e3a757663693a30313a41543a313038303738343346393441454530454535303933464243323534424438313350626d706c45552f312f32302f313532386269736e424d5347504b20417573747269616273640262746769383430353339303036636e616da463666e74754d5553544552465241553c474f455353494e47455262666e754d7573746572667261752d47c3b6c39f696e67657263676e74684741425249454c4562676e684761627269656c656376657265312e302e3063646f626a313939382d30322d32365840fc544db09e37eeb2f88d6ec0e6c16d4f20d8c3f899acd680126c7c8f7743c95b54956f88bf3f92631370e831f5461218b81c75cc728edbd8e98db2d52125f2ba".fromHexString()
    val pubKey = CoseJsEcPubKey(
        "add55cf5ad1b96d47a8e6d413d3037bb473224d60ab85d6e464f21ee1d38f970".fromHexString(),
        "5127d9181edfbfa120d7c2659728ce9c1029dc9aa68acf50fd5313b516974177".fromHexString(),
        CurveIdentifier.P256
    )
    Cose.verify(signedBitString, pubKey).then { println("Signature successfully verified!") }

    val cborJson = Cbor.decode(signedBitString)
    val protectedHeader = cborJson["value"][0]
    val unprotectedHeader = cborJson["value"][1]
    val content = cborJson["value"][2]
    val signature = cborJson["value"][3]
    val protectedHeaderCbor = Cbor.decode(protectedHeader)
    val kid = protectedHeaderCbor.get(4) as Uint8Array? ?:
            if (unprotectedHeader.length !== undefined)
               Cbor.decode(unprotectedHeader).get(1) as Uint8Array
            else
                throw IllegalArgumentException("KID not found")
    if (kid === undefined)
        throw IllegalArgumentException("KID not found")
    val algorithm = protectedHeaderCbor.get(1)
    console.info(kid) // is a Uint8Array
    console.info(algorithm) // is -7, so ECDSA_256
    val kidHex = (0 until kid.length).map { i -> kid[i].toString(16) }.joinToString("")
    if (kidHex != "d919375fc1e7b6b2") throw IllegalArgumentException("KID not equal to Austrian DSC")

    signedBitString[0] = 0
    Cose.verify(signedBitString, pubKey).catch { println("Could not verify with broken header: $it") }

    val pemCert="30 82 04 8a 30 82 03 72 a0 03 02 01 02 02 11 00 ba 24 06 19 3d 71 98 d1 09 00 00 00 00 62 16 f6 30 0d 06 09 2a 86 48 86 f7 0d 01 01 0b 05 00 30 46 31 0b 30 09 06 03 55 04 06 13 02 55 53 31 22 30 20 06 03 55 04 0a 13 19 47 6f 6f 67 6c 65 20 54 72 75 73 74 20 53 65 72 76 69 63 65 73 20 4c 4c 43 31 13 30 11 06 03 55 04 03 13 0a 47 54 53 20 43 41 20 31 43 33 30 1e 17 0d 32 31 30 34 31 33 31 30 34 32 35 38 5a 17 0d 32 31 30 37 30 36 31 30 34 32 35 37 5a 30 19 31 17 30 15 06 03 55 04 03 13 0e 77 77 77 2e 67 6f 6f 67 6c 65 2e 63 6f 6d 30 59 30 13 06 07 2a 86 48 ce 3d 02 01 06 08 2a 86 48 ce 3d 03 01 07 03 42 00 04 aa 7e 92 6d cf 2c e5 f5 23 29 b5 84 72 af fa 30 fa 25 c5 52 08 70 75 58 8b bc b5 e7 0d 3d dd a2 c0 31 30 e9 34 6c 88 84 96 63 a8 1c f2 73 24 06 a2 48 d7 b2 50 91 78 d4 05 59 81 79 b5 c3 80 04 a3 82 02 69 30 82 02 65 30 0e 06 03 55 1d 0f 01 01 ff 04 04 03 02 07 80 30 13 06 03 55 1d 25 04 0c 30 0a 06 08 2b 06 01 05 05 07 03 01 30 0c 06 03 55 1d 13 01 01 ff 04 02 30 00 30 1d 06 03 55 1d 0e 04 16 04 14 a0 8e 47 36 23 5b 70 58 20 ff ea e8 68 e3 91 7d 68 a0 78 78 30 1f 06 03 55 1d 23 04 18 30 16 80 14 8a 74 7f af 85 cd ee 95 cd 3d 9c d0 e2 46 14 f3 71 35 1d 27 30 6a 06 08 2b 06 01 05 05 07 01 01 04 5e 30 5c 30 27 06 08 2b 06 01 05 05 07 30 01 86 1b 68 74 74 70 3a 2f 2f 6f 63 73 70 2e 70 6b 69 2e 67 6f 6f 67 2f 67 74 73 31 63 33 30 31 06 08 2b 06 01 05 05 07 30 02 86 25 68 74 74 70 3a 2f 2f 70 6b 69 2e 67 6f 6f 67 2f 72 65 70 6f 2f 63 65 72 74 73 2f 67 74 73 31 63 33 2e 64 65 72 30 19 06 03 55 1d 11 04 12 30 10 82 0e 77 77 77 2e 67 6f 6f 67 6c 65 2e 63 6f 6d 30 21 06 03 55 1d 20 04 1a 30 18 30 08 06 06 67 81 0c 01 02 01 30 0c 06 0a 2b 06 01 04 01 d6 79 02 05 03 30 3c 06 03 55 1d 1f 04 35 30 33 30 31 a0 2f a0 2d 86 2b 68 74 74 70 3a 2f 2f 63 72 6c 73 2e 70 6b 69 2e 67 6f 6f 67 2f 67 74 73 31 63 33 2f 51 4f 76 4a 30 4e 31 73 54 32 41 2e 63 72 6c 30 82 01 06 06 0a 2b 06 01 04 01 d6 79 02 04 02 04 81 f7 04 81 f4 00 f2 00 77 00 7d 3e f2 f8 8f ff 88 55 68 24 c2 c0 ca 9e 52 89 79 2b c5 0e 78 09 7f 2e 6a 97 68 99 7e 22 f0 d7 00 00 01 78 cb 0a b2 93 00 00 04 03 00 48 30 46 02 21 00 e6 e2 c0 b9 28 cb a7 fe 77 d0 94 a0 2a f8 13 9f 4d 4b a1 a8 5f 1f 8d 95 f6 e3 de 0a c2 05 e8 a2 02 21 00 fd eb 3a 12 4a fd 1c 01 10 64 0a 5a 1b 28 93 ed e2 6e b9 49 dc 58 da 4c 06 2f ad f2 6c cd 42 f2 00 77 00 ee c0 95 ee 8d 72 64 0f 92 e3 c3 b9 1b c7 12 a3 69 6a 09 7b 4b 6a 1a 14 38 e6 47 b2 cb ed c5 f9 00 00 01 78 cb 0a b2 70 00 00 04 03 00 48 30 46 02 21 00 bb c5 65 20 ac bd cc 23 d1 41 7a c7 0c 8b 75 6b 3a 35 c6 df 5f 63 32 74 c8 7e d2 ec f3 41 3b 64 02 21 00 8e 3b cf 71 4c 9f 9c f4 11 73 9f fd bc 78 f3 00 15 b5 1f 16 9b f0 1c 6b e0 d0 0f 8c 92 be 6b f4 30 0d 06 09 2a 86 48 86 f7 0d 01 01 0b 05 00 03 82 01 01 00 3d f7 0e 24 4e 5e a9 23 b3 b2 6f 1e d2 f3 4a fa de 2b 25 0d 10 43 23 a1 4d c6 42 3c d2 8e 14 07 05 3e f9 28 20 4e 4b 9f c9 8f 67 36 90 84 00 dc 0b 14 4d 66 72 23 f6 8c af 75 eb 93 e7 d9 e9 42 11 1b 74 0e 92 34 05 6d cd 38 84 dd 49 de cd b4 73 29 12 85 8f 96 98 34 6d 44 57 e9 7a 87 c8 90 7d 32 14 3d 07 2d e8 35 ef d2 1e 4a 2a 4f bd 15 fe fb 79 2d e3 ca 88 cf 96 15 79 3e 37 84 5f d6 d1 b1 dc 45 ea dd 4e 3c 44 ff 64 99 20 44 a5 a2 3a 27 6b 33 02 ea 77 83 4a 6e fa 4e 94 d8 e9 55 4f df 5b 51 2c 3b 1b da 44 b9 eb 75 a6 2a 5e 60 7c 40 98 36 ed d5 09 5f 4f 26 35 4b 11 43 7b c3 fd 44 9b a9 08 b1 cd 53 a5 b6 e8 d2 a2 24 3c f7 ec cd 03 66 da 96 99 0f 63 e9 21 be 49 2b 0f 2b 45 33 cf 9e e5 17 62 21 7e 8d 3e ca 65 ab d6 c0 60 50 c3 1e 7b 77 49 95 07 07 3e 6a 4f f4 c7 39"
    val b64=pemCert.split(' ').joinToString(separator = "").fromHexString()
   val bytes= Uint8Array(b64.toTypedArray())
    val certificate = fromBER(bytes.buffer)
    val res= certificate.result
    println("Cert:")
    console.info(Certificate(js("({'schema':res})")))
    println("end cert")
    console.info(certificate)
}


