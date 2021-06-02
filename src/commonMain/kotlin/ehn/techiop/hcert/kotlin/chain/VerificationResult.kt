package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

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
     * Indicates, which content actually has been decoded
     */
    var content: MutableList<ContentType> = mutableListOf()

    /**
     * Holds the error, if any occurred
     */
    var error: Error? = null;

    /**
     * From Swift ValidationCore
     */
    enum class Error {
        GENERAL_ERROR,
        INVALID_SCHEME_PREFIX,
        DECOMPRESSION_FAILED,
        BASE_45_DECODING_FAILED,
        COSE_DESERIALIZATION_FAILED,
        CBOR_DESERIALIZATION_FAILED,
        CWT_EXPIRED,
        QR_CODE_ERROR,
        CERTIFICATE_QUERY_FAILED,
        USER_CANCELLED,
        TRUST_SERVICE_ERROR,
        KEY_NOT_IN_TRUST_LIST,
        PUBLIC_KEY_EXPIRED,
        UNSUITABLE_PUBLIC_KEY_TYPE,
        KEY_CREATION_ERROR,
        KEYSTORE_ERROR,
        SIGNATURE_INVALID,
        CONTEXT_IDENTIFIER_INVALID, // new
    }

    override fun toString(): String {
        return "VerificationResult(" +
                "expirationTime=$expirationTime, " +
                "issuedAt=$issuedAt, " +
                "issuer=$issuer, " +
                "certificateValidFrom=$certificateValidFrom, " +
                "certificateValidUntil=$certificateValidUntil, " +
                "certificateValidContent=$certificateValidContent, " +
                "content=$content, " +
                "error=$error" +
                ")"
    }

}