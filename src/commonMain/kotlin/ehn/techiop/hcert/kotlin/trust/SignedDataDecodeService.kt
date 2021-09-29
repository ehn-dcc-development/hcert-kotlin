package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.jvm.JvmOverloads
import kotlin.time.Duration


/**
 * Decodes a [SignedData] structure, and verify the validity of it
 */
class SignedDataDecodeService @JvmOverloads constructor(
    private val repository: CertificateRepository,
    private val clock: Clock = Clock.System,
    private val clockSkewSeconds: Int = 300
) {

    @Throws(VerificationException::class)
    fun decode(input: SignedData, headersToParse: List<CoseHeaderKeys> = listOf()): SignedDataParsed {
        val cose = CoseAdapter(input.signature)
        val kid = cose.getProtectedAttributeByteArray(CoseHeaderKeys.KID.intVal)
            ?: throw VerificationException(
                Error.TRUST_LIST_SIGNATURE_INVALID,
                "KID not defined",
                details = mapOf("kid" to "null")
            )

        val validated = cose.validate(kid, repository)
        if (!validated)
            throw VerificationException(Error.TRUST_LIST_SIGNATURE_INVALID, "Not validated")

        val headers = headersToParse.map { it to cose.getProtectedAttributeInt(it.intVal) }.toMap()
        val actualHash = Hash(input.content).calc()

        val map = cose.getContentMap()
        val expectedHash = map.getByteArray(CwtHeaderKeys.SUBJECT.intVal)
        if (!(expectedHash contentEquals actualHash))
            throw VerificationException(Error.TRUST_LIST_SIGNATURE_INVALID, "Hash not matching")

        val notBefore = map.getNumber(CwtHeaderKeys.NOT_BEFORE.intVal)
            ?: throw VerificationException(
                Error.TRUST_LIST_NOT_YET_VALID,
                "NotBefore=null",
                details = mapOf("validFrom" to "null")
            )

        val validFrom = Instant.fromEpochSeconds(notBefore.toLong())
        if (validFrom > clock.now().plus(Duration.seconds(clockSkewSeconds)))
            throw VerificationException(
                Error.TRUST_LIST_NOT_YET_VALID,
                "NotBefore>clock.now()",
                details = mapOf("validFrom" to validFrom.toString(), "currentTime" to clock.now().toString())
            )

        val expiration = map.getNumber(CwtHeaderKeys.EXPIRATION.intVal)
            ?: throw VerificationException(
                Error.TRUST_LIST_EXPIRED, "Expiration=null",
                details = mapOf("expirationTime" to "null")
            )

        val validUntil = Instant.fromEpochSeconds(expiration.toLong())
        if (validUntil < clock.now().minus(Duration.seconds(clockSkewSeconds)))
            throw VerificationException(
                Error.TRUST_LIST_EXPIRED, "Expiration<clock.now()",
                details = mapOf("expirationTime" to validUntil.toString(), "currentTime" to clock.now().toString())
            )

        return SignedDataParsed(validFrom, validUntil, input.content, headers)
    }

}
