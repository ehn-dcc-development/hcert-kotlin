package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
class MetaInformation {

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
     * Indicates, which content actually has been decoded
     */
    var content: MutableList<ContentType> = mutableListOf()


    override fun toString(): String {
        return "VerificationResult(" +
                "expirationTime=$expirationTime, " +
                "issuedAt=$issuedAt, " +
                "issuer=$issuer, " +
                "certificateValidFrom=$certificateValidFrom, " +
                "certificateValidUntil=$certificateValidUntil, " +
                "certificateValidContent=$certificateValidContent, " +
                "content=$content" +
                ")"
    }

    companion object {
        fun from(verificationResult: VerificationResult) = MetaInformation().also {
            it.expirationTime = verificationResult.expirationTime
            it.issuedAt = verificationResult.issuedAt
            it.issuer = verificationResult.issuer
            it.certificateValidFrom = verificationResult.certificateValidFrom
            it.certificateValidUntil = verificationResult.certificateValidUntil
            it.certificateValidContent = verificationResult.certificateValidContent
            it.content = verificationResult.content
        }
    }

}