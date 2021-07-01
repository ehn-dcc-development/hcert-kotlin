package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.Error.TRUST_LIST_EXPIRED
import ehn.techiop.hcert.kotlin.chain.Error.TRUST_LIST_NOT_YET_VALID
import ehn.techiop.hcert.kotlin.chain.Error.TRUST_LIST_SIGNATURE_INVALID
import ehn.techiop.hcert.kotlin.chain.Error.TRUST_SERVICE_ERROR
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlin.time.Duration

/**
 * Decodes two binary blobs, expected to contain the content and signature of a [TrustListV2]
 *
 * [repository] contains the trust anchor for the parsed file
 * [clock] defines the current time to use for validity checks
 * [clockSkew] defines the error margin when comparing time validity of the parsed file
 */
class TrustListDecodeService(
    private val repository: CertificateRepository,
    private val clock: Clock = Clock.System,
    private val clockSkew: Duration = Duration.seconds(300)
) {

    /**
     * See [ContentAndSignature] for details about the content
     * If all checks succeed, [trustList.content] is parsed as a [TrustListV2], and the certificates are and returned
     */
    fun decode(trustList: ContentAndSignature): List<TrustedCertificate> {
        val cose = CoseAdapter(trustList.signature)
        val optionalContent = trustList.content
        val kid = cose.getProtectedAttributeByteArray(CoseHeaderKeys.KID.intVal)
            ?: throw VerificationException(TRUST_LIST_SIGNATURE_INVALID, "KID not defined")

        val validated = cose.validate(kid, repository)
        if (!validated)
            throw VerificationException(TRUST_LIST_SIGNATURE_INVALID, "Not validated")

        val version = cose.getProtectedAttributeInt(CoseHeaderKeys.TRUSTLIST_VERSION.intVal)
        if (version == 1) {
            throw VerificationException(TRUST_SERVICE_ERROR, "Version 1")
        } else if (version == 2) {
            val actualHash = Hash(optionalContent).calc()

            val map = cose.getContentMap()
            val expectedHash = map.getByteArray(CwtHeaderKeys.SUBJECT.intVal)
            if (!(expectedHash contentEquals actualHash))
                throw VerificationException(TRUST_LIST_SIGNATURE_INVALID, "Hash not matching")

            val notBefore = map.getNumber(CwtHeaderKeys.NOT_BEFORE.intVal)
                ?: throw VerificationException(TRUST_LIST_NOT_YET_VALID, "NotBefore=null")

            val validFrom = Instant.fromEpochSeconds(notBefore.toLong())
            if (validFrom > clock.now().plus(clockSkew))
                throw VerificationException(TRUST_LIST_NOT_YET_VALID, "NotBefore>clock.now()")

            val expiration = map.getNumber(CwtHeaderKeys.EXPIRATION.intVal)
                ?: throw VerificationException(TRUST_LIST_EXPIRED, "Expiration=null")

            val validUntil = Instant.fromEpochSeconds(expiration.toLong())
            if (validUntil < clock.now().minus(clockSkew))
                throw VerificationException(TRUST_LIST_EXPIRED, "Expiration<clock.now()")

            return Cbor.decodeFromByteArray<TrustListV2>(optionalContent).certificates
        } else {
            throw VerificationException(TRUST_SERVICE_ERROR, "Version unknown")
        }
    }

}

