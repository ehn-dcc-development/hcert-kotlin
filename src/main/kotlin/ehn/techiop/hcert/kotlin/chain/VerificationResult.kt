package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.trust.ContentType
import java.time.Instant

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
     * The compressed CWT is encoded as ASCII using Base45
     */
    var base45Decoded = false

    /**
     * `HC1:` SHALL be used as a prefix in the Base45 encoded data
     */
    var contextIdentifier: String? = null

    /**
     * CWT SHALL be compressed using ZLIB
     */
    var zlibDecoded = false

    /**
     * COSE signature MUST be verifiable
     */
    var coseVerified = false

    /**
     * The payload is structured and encoded as a CBOR with a COSE digital signature.
     */
    var cborDecoded = false

    // TODO Schema validation?

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
    var certificateValidContent: List<ContentType> = listOf(ContentType.TEST, ContentType.VACCINATION, ContentType.RECOVERY)

    /**
     * Indicates, which content actually has been decoded
     */
    var content: MutableList<ContentType> = mutableListOf()


    override fun toString(): String {
        return "VerificationResult(" +
                "expirationTime=$expirationTime, " +
                "issuedAt=$issuedAt, " +
                "issuer=$issuer, " +
                "base45Decoded=$base45Decoded, " +
                "contextIdentifier=$contextIdentifier, " +
                "zlibDecoded=$zlibDecoded, " +
                "coseVerified=$coseVerified, " +
                "cborDecoded=$cborDecoded, " +
                "certificateValidFrom=$certificateValidFrom, " +
                "certificateValidUntil=$certificateValidUntil, " +
                "certificateValidContent=$certificateValidContent, " +
                "content=$content" +
                ")"
    }


}