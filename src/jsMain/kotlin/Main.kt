import Asn1js.fromBER
import ehn.techiop.hcert.kotlin.chain.DefaultChain
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.common.Base45Encoder
import ehn.techiop.hcert.kotlin.chain.fromHexString
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.crypto.*
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
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
    val kid = protectedHeaderCbor.get(4) as Uint8Array? ?: if (unprotectedHeader.length !== undefined)
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


    val dscPem = "-----BEGIN CERTIFICATE-----\n" +
            "MIIBvTCCAWOgAwIBAgIKAXk8i88OleLsuTAKBggqhkjOPQQDAjA2MRYwFAYDVQQD\n" +
            "DA1BVCBER0MgQ1NDQSAxMQswCQYDVQQGEwJBVDEPMA0GA1UECgwGQk1TR1BLMB4X\n" +
            "DTIxMDUwNTEyNDEwNloXDTIzMDUwNTEyNDEwNlowPTERMA8GA1UEAwwIQVQgRFND\n" +
            "IDExCzAJBgNVBAYTAkFUMQ8wDQYDVQQKDAZCTVNHUEsxCjAIBgNVBAUTATEwWTAT\n" +
            "BgcqhkjOPQIBBggqhkjOPQMBBwNCAASt1Vz1rRuW1HqObUE9MDe7RzIk1gq4XW5G\n" +
            "TyHuHTj5cFEn2Rge37+hINfCZZcozpwQKdyaporPUP1TE7UWl0F3o1IwUDAOBgNV\n" +
            "HQ8BAf8EBAMCB4AwHQYDVR0OBBYEFO49y1ISb6cvXshLcp8UUp9VoGLQMB8GA1Ud\n" +
            "IwQYMBaAFP7JKEOflGEvef2iMdtopsetwGGeMAoGCCqGSM49BAMCA0gAMEUCIQDG\n" +
            "2opotWG8tJXN84ZZqT6wUBz9KF8D+z9NukYvnUEQ3QIgdBLFSTSiDt0UJaDF6St2\n" +
            "bkUQuVHW6fQbONd731/M4nc=\n" +
            "-----END CERTIFICATE-----\n"
    val chain = DefaultChain.buildVerificationChain(PrefilledCertificateRepository(dscPem))
    val qrCode = "HC1:NCFOXN%TS3DH3ZSUZK+.V0ETD%65NL-AH%TAIOOP-I*WA1778WAKQKMYSKD93B4:ZH6I1\$4JN:IN1MKK9+OC*PP:+P*.1D9R+Q6646C%6RF6:X93O5RF6\$T61R6B46646VY9WC5ME65H1KD34LT HBSZ4GH0B69X5Q/36MZ5BTMUW5-5QNF6O M9R1RF6ECM676746C0FFS6NWE0Y6Z EJZ6KS6YQEE%61Y6LMEA46B 17PPDFPVX1R270:6NEQ0R6AOM6PPL4Q0RUS-EBHU68E1W5XC52T9UF5LDCPF5RBQ746B46O1N646BQ99Q9E\$B ZJY1B QTOD3CQSP\$SR\$S3NDC9UOD3Y.TJET9G3DZI65BDM14NJ*XI-XIFRLL:F*ZR.OVOIF/HLTNP8EFNC3P:HDD8B1MM1M9NTNC30GH.Z8VHL+KLF%CD 810H% 0R%0ZD5CC9T0H\$/I*44SA3ZX8AT5XVMZGU\$X9+-5SL91PR%F6PH4R8Q0\$9UB9VXM0OPMR1A*CS:3.+4J0V/ED.EL5OR-AV.EJ4Z8WDRM V06PW1LRED0RLSZ18BN0BWF10*4Q50 "
    val verificationResult = VerificationResult()
    val greenCertificate = chain.decode(qrCode, verificationResult)
    console.info(verificationResult)
    console.info(greenCertificate)

}


