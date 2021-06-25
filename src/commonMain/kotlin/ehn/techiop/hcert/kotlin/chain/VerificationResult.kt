package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.js.JsName

@Serializable
class VerificationResult {

    /**
     * `exp` claim SHALL hold a timestamp.
     * Verifier MUST reject the payload after expiration.
     * It MUST not exceed the validity period of the DSC.
     */
    @JsName("expirationTime")
    var expirationTime: Instant? = null

    /**
     * `iat` claim SHALL hold a timestamp. It MUST not predate the validity period of the DSC.
     */
    @JsName("issuedAt")
    var issuedAt: Instant? = null

    /**
     * `iss` claim MAY hold ISO 3166-1 alpha-2 country code
     */
    @JsName("issuer")
    var issuer: String? = null

    /**
     * Lifetime of certificate used for verification of COSE
     */
    @JsName("certificateValidFrom")
    var certificateValidFrom: Instant? = null

    /**
     * Lifetime of certificate used for verification of COSE
     */
    @JsName("certificateValidUntil")
    var certificateValidUntil: Instant? = null

    /**
     * Indicates, which content may be signed with the certificate, defaults to all content types
     */
    @JsName("certificateValidContent")
    var certificateValidContent: List<ContentType> = ContentType.values().toList()

    /**
     * Indicates, which content actually has been decoded
     */
    @JsName("content")
    var content: MutableList<ContentType> = mutableListOf()

    /**
     * Holds the error, if any occurred
     */
    @JsName("error")
    var error: Error? = null

    override fun toString(): String {
        return "VerificationResult(" +
                "expirationTime=$expirationTime, " +
                "issuedAt=$issuedAt, " +
                "issuer=$issuer, " +
                "certificateValidFrom=$certificateValidFrom, " +
                "certificateValidUntil=$certificateValidUntil, " +
                "certificateValidContent=$certificateValidContent, " +
                "content=$content, " +
                "error=$error, " +
                ")"
    }

    fun setCertificateData(certificate: CertificateAdapter) {
        certificateValidFrom = certificate.validFrom
        certificateValidUntil = certificate.validUntil
        certificateValidContent = certificate.validContentTypes
    }

}
