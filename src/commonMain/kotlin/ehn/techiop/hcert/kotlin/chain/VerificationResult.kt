package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * See also VerificationResultJs
 */
@Serializable
class VerificationResult {

    /**
     * `exp` claim SHALL hold a timestamp.
     * Verifier MUST reject the payload after expiration.
     * It MUST not exceed the validity period of the DSC.
     */
    var expirationTime: Instant? = null

    /**
     * `iat` claim SHALL hold a timestamp. It MUST not predate the validity period of the DSC.
     */
    var issuedAt: Instant? = null

    /**
     * `iss` claim MAY hold ISO 3166-1 alpha-2 country code
     */
    var issuer: String? = null

    /**
     * Lifetime of certificate used for verification of COSE
     */
    var certificateValidFrom: Instant? = null

    /**
     * Lifetime of certificate used for verification of COSE
     */
    var certificateValidUntil: Instant? = null

    /**
     * Indicates, which content may be signed with the certificate, defaults to all content types
     */
    var certificateValidContent: List<ContentType> = ContentType.values().toList()

    /**
     * Contains the issuing country of the certificate that signed the HCERT data,
     * i.e. the "C=" entry of the subject
     */
    var certificateSubjectCountry: String? = null

    /**
     * Indicates, which content actually has been decoded
     */
    var content: MutableList<ContentType> = mutableListOf()

    /**
     * Holds the error, if any occurred
     */
    var error: Error? = null

    override fun toString(): String {
        return "VerificationResult(" +
                "expirationTime=$expirationTime, " +
                "issuedAt=$issuedAt, " +
                "issuer=$issuer, " +
                "certificateValidFrom=$certificateValidFrom, " +
                "certificateValidUntil=$certificateValidUntil, " +
                "certificateValidContent=$certificateValidContent, " +
                "certificateSubjectCountry=$certificateSubjectCountry, " +
                "content=$content, " +
                "error=$error, " +
                ")"
    }

    fun setCertificateData(certificate: CertificateAdapter) {
        certificateValidFrom = certificate.validFrom
        certificateValidUntil = certificate.validUntil
        certificateValidContent = certificate.validContentTypes
        certificateSubjectCountry = certificate.subjectCountry
    }

}
