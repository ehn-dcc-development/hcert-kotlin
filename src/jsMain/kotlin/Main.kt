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

    val dsc="MIIC6DCCAdCgAwIBAgIFAMFkOjcwDQYJKoZIhvcNAQELBQAwETEPMA0GA1UEAwwGUlNBLU1lMB4XDTIxMDUwMzE4MDAwMFoXDTIxMDYwMjE4MDAwMFowETEPMA0GA1UEAwwGUlNBLU1lMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz2ixJL06PlaxpRD3bWwIWN25x4gMav4A8oJOYh9g9Rt1s8PGto9zXfAwZ/md9h9CwDkHSaU2Sssj5jvp0yZpO8fLiEZcX5BnriC65CvYDGpwnvvF9XtpuZsT5OJ/vokAMSFgIoBEWt5LzC7vTP7GEBFshysossEr1S+HRCIxZjJt9tstEm156PlRB40SCp9lU1d3BRpHj3uuPCwsYSsO25Yk/1fQJU1AbmNmdbEKL5VEgbcHyL1TkGOITF6T5DTAJNCzl/UMezEBizK7b1RpTUI9TOV6xr9kzZY/wQ05fJ02xISnC/6hKi4a83fZO/ADelr2eeOS4PWrTeh9CgbY3wIDAQABo0cwRTAOBgNVHQ8BAf8EBAMCBaAwMwYDVR0lBCwwKgYMKwYBBAEAjjePZQEBBgwrBgEEAQCON49lAQIGDCsGAQQBAI43j2UBAzANBgkqhkiG9w0BAQsFAAOCAQEAsYrUM/J16wTBZxeWTszAVxXiKirwoV9Nt82WW9vn6Px5tZZ5m3yazfZeARkokhcvUZ9U0VDLgX09Yzj0NjSLc82WknIxf5FT3NhDE8ax7VoOH17pv2YflNP0bXO2YQqX/7XFMfHaZ5KKdQmThht7BVYRKZR8Ksi2TvnFxnBdLNcWl41LNaNpM5vSjZ236OhWBNZrFx4OD2XLEJkk6NZ4txrsHmpSogyIlF9kextqxEqg+9aluWQyiz4Q4HcIPd1S7M/CsanO4URrLLKKmBzaaQV2JBoTwEBZ5NiZj3LpfaxsbiyGnwYxMlb0RO2V/8JvKYNOWgl7tVQk7+Uut/MHrg=="
    val jsCertificate = JsCertificate(dsc)
    console.info(jsCertificate)
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


