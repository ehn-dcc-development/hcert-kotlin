package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.Error
import ehn.techiop.hcert.kotlin.chain.VerificationException
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration


/**
 * Decodes a [SignedData] structure, and verify the validity of it
 */
class SignedDataDecodeService constructor(
    private val repository: CertificateRepository,
    private val clock: Clock = Clock.System,
    private val clockSkew: Duration = Duration.seconds(300)
) {

    @Throws(VerificationException::class)
    fun decode(input: SignedData, headersToParse: List<CoseHeaderKeys> = listOf()): SignedDataParsed {
        // TODO Error Codes are for trust list
        val cose = CoseAdapter(input.signature)
        val kid = cose.getProtectedAttributeByteArray(CoseHeaderKeys.KID.intVal)
            ?: throw VerificationException(Error.TRUST_LIST_SIGNATURE_INVALID, "KID not defined")

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
            ?: throw VerificationException(Error.TRUST_LIST_NOT_YET_VALID, "NotBefore=null")

        val validFrom = Instant.fromEpochSeconds(notBefore.toLong())
        if (validFrom > clock.now().plus(clockSkew))
            throw VerificationException(Error.TRUST_LIST_NOT_YET_VALID, "NotBefore>clock.now()")

        val expiration = map.getNumber(CwtHeaderKeys.EXPIRATION.intVal)
            ?: throw VerificationException(Error.TRUST_LIST_EXPIRED, "Expiration=null")

        val validUntil = Instant.fromEpochSeconds(expiration.toLong())
        if (validUntil < clock.now().minus(clockSkew))
            throw VerificationException(Error.TRUST_LIST_EXPIRED, "Expiration<clock.now()")

        return SignedDataParsed(validFrom, validUntil, input.content, headers)
    }

}


data class SignedDataParsed(
    val notBefore: Instant,
    val notAfter: Instant,
    val content: ByteArray,
    // TODO how to get protected headers to any type
    val headers: Map<CoseHeaderKeys, Int?>
)