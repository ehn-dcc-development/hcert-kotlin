import Asn1js.fromBER
import ehn.techiop.hcert.kotlin.chain.DefaultChain
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.common.Base45Encoder
import ehn.techiop.hcert.kotlin.chain.fromHexString
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.crypto.*
import ehn.techiop.hcert.kotlin.data.*
import kotlinx.browser.window
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.internal.JSJoda.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import pkijs.src.Certificate.Certificate
import org.w3c.fetch.Request
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Date


fun main() {
    // from https://dgc.a-sit.at/ehn/testsuite
    val signedBitString =
        "d2844da20448d919375fc1e7b6b20126a0590124a4041a60a4d36d061a60a2306d01624154390103a101a4617681aa62646e01626d616d4f52472d3130303033303231356276706a313131393330353030356264746a323032312d30322d313862636f624154626369783075726e3a757663693a30313a41543a313038303738343346393441454530454535303933464243323534424438313350626d706c45552f312f32302f313532386269736e424d5347504b20417573747269616273640262746769383430353339303036636e616da463666e74754d5553544552465241553c474f455353494e47455262666e754d7573746572667261752d47c3b6c39f696e67657263676e74684741425249454c4562676e684761627269656c656376657265312e302e3063646f626a313939382d30322d32365840fc544db09e37eeb2f88d6ec0e6c16d4f20d8c3f899acd680126c7c8f7743c95b54956f88bf3f92631370e831f5461218b81c75cc728edbd8e98db2d52125f2ba".fromHexString()
    val pubKey = CoseJsEcPubKey(
        "add55cf5ad1b96d47a8e6d413d3037bb473224d60ab85d6e464f21ee1d38f970".fromHexString(),
        "5127d9181edfbfa120d7c2659728ce9c1029dc9aa68acf50fd5313b516974177".fromHexString(),
        CurveIdentifier.P256
    )
    //Cose.verify(signedBitString, pubKey).then { println("Signature successfully verified!") }

    //val kidHex = (0 until kid.length).map { i -> kid[i].toString(16) }.joinToString("")
    //if (kidHex != "d919375fc1e7b6b2") throw IllegalArgumentException("KID not equal to Austrian DSC")


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
    val dscHex= "30 82 01 bd 30 82 01 63 a0 03 02 01 02 02 0a 01 79 3c 8b cf 0e 95 e2 ec b9 30 0a 06 08 2a 86 48 ce 3d 04 03 02 30 36 31 16 30 14 06 03 55 04 03 0c 0d 41 54 20 44 47 43 20 43 53 43 41 20 31 31 0b 30 09 06 03 55 04 06 13 02 41 54 31 0f 30 0d 06 03 55 04 0a 0c 06 42 4d 53 47 50 4b 30 1e 17 0d 32 31 30 35 30 35 31 32 34 31 30 36 5a 17 0d 32 33 30 35 30 35 31 32 34 31 30 36 5a 30 3d 31 11 30 0f 06 03 55 04 03 0c 08 41 54 20 44 53 43 20 31 31 0b 30 09 06 03 55 04 06 13 02 41 54 31 0f 30 0d 06 03 55 04 0a 0c 06 42 4d 53 47 50 4b 31 0a 30 08 06 03 55 04 05 13 01 31 30 59 30 13 06 07 2a 86 48 ce 3d 02 01 06 08 2a 86 48 ce 3d 03 01 07 03 42 00 04 ad d5 5c f5 ad 1b 96 d4 7a 8e 6d 41 3d 30 37 bb 47 32 24 d6 0a b8 5d 6e 46 4f 21 ee 1d 38 f9 70 51 27 d9 18 1e df bf a1 20 d7 c2 65 97 28 ce 9c 10 29 dc 9a a6 8a cf 50 fd 53 13 b5 16 97 41 77 a3 52 30 50 30 0e 06 03 55 1d 0f 01 01 ff 04 04 03 02 07 80 30 1d 06 03 55 1d 0e 04 16 04 14 ee 3d cb 52 12 6f a7 2f 5e c8 4b 72 9f 14 52 9f 55 a0 62 d0 30 1f 06 03 55 1d 23 04 18 30 16 80 14 fe c9 28 43 9f 94 61 2f 79 fd a2 31 db 68 a6 c7 ad c0 61 9e 30 0a 06 08 2a 86 48 ce 3d 04 03 02 03 48 00 30 45 02 21 00 c6 da 8a 68 b5 61 bc b4 95 cd f3 86 59 a9 3e b0 50 1c fd 28 5f 03 fb 3f 4d ba 46 2f 9d 41 10 dd 02 20 74 12 c5 49 34 a2 0e dd 14 25 a0 c5 e9 2b 76 6e 45 10 b9 51 d6 e9 f4 1b 38 d7 7b df 5f cc e2 77".split(' ').joinToString(separator = "").fromHexString()
    val jsCertificate = JsCertificate(dscHex)
    console.info(Json.encodeToString(jsCertificate.getValidFrom()))
    console.info(Json.encodeToString(jsCertificate.getValidUntil()))
    console.info(Json.encodeToString(jsCertificate.toTrustedCertificate()))
    console.info(Json.encodeToString(jsCertificate.calcKid()))
    val chain = DefaultChain.buildVerificationChain(PrefilledCertificateRepository(jsCertificate))
    window.fetch(Request("qr.txt")).then(onFulfilled = {
        it.text().then(onFulfilled = {
            val qrCode = it.trimIndent().trim()
            val verificationResult = VerificationResult()
            val greenCertificate = chain.decode(qrCode, verificationResult)
            console.info("Success")
            console.info(verificationResult)
            console.info(greenCertificate)
        })})

}


