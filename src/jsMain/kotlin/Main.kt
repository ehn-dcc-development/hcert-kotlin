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
    //val kidHex = (0 until kid.length).map { i -> kid[i].toString(16) }.joinToString("")
    //if (kidHex != "d919375fc1e7b6b2") throw IllegalArgumentException("KID not equal to Austrian DSC")

    val dsc="MIIBPTCB5KADAgECAgUAkDsaQjAKBggqhkjOPQQDAjAQMQ4wDAYDVQQDDAVFQy1NZTAeFw0yMTA1MDMxODAwMDBaFw0yMTA2MDIxODAwMDBaMBAxDjAMBgNVBAMMBUVDLU1lMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEYR/hL45xPlXQ2s8H00wAMjxfFAn+jHT37HQzT3ZLdeu9MkWAidRBUyS+2qwr/o4b0AXo/xUxocKmYfkTSQUpX6MrMCkwDgYDVR0PAQH/BAQDAgWgMBcGA1UdJQQQMA4GDCsGAQQBAI43j2UBAzAKBggqhkjOPQQDAgNIADBFAiEAmJe8cQrHia1NuqE4ihhhPp45gFdJVWweejE66QE3S10CIBhctfZeQ0kTZ3TqPQpiI104QAOM4MDNLUkoo5/w8MjE"
    val jsCertificate = JsCertificate(dsc)
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


