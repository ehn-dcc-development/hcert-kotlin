package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.crypto.CwtHeaderKeys
import kotlinx.datetime.Clock
import kotlin.time.Duration


/**
 * Encodes arbitrary content as a [SignedData] object
 */
class SignedDataEncodeService constructor(
    private val signingService: CryptoService,
    private val validity: Duration = Duration.hours(48),
    private val clock: Clock = Clock.System,
) {

    /**
     * Signature is a COSE structure with protected header keys
     * - [CoseHeaderKeys.TRUSTLIST_VERSION]
     * - [CoseHeaderKeys.KID]: KID of the [signingService]'s certificate
     * The content of that COSE structure is a CWT map containing
     * - [CwtHeaderKeys.NOT_BEFORE]: seconds since UNIX epoch
     * - [CwtHeaderKeys.EXPIRATION]: seconds since UNIX epoch
     * - [CwtHeaderKeys.SUBJECT]: the SHA-256 hash of the content file
     */
    private fun encodeSignature(content: ByteArray, coseHeaderKeys: Map<CoseHeaderKeys, Any> = mapOf()): ByteArray {
        val validFrom = clock.now()
        val validUntil = validFrom + validity
        val hash = Hash(content).calc()
        val cwt = CwtCreationAdapter()
        cwt.add(CwtHeaderKeys.NOT_BEFORE.intVal, validFrom.epochSeconds)
        cwt.add(CwtHeaderKeys.EXPIRATION.intVal, validUntil.epochSeconds)
        cwt.add(CwtHeaderKeys.SUBJECT.intVal, hash)
        val cose = CoseCreationAdapter(cwt.encode())
        signingService.getCborHeaders().forEach {
            cose.addProtectedAttribute(it.first, it.second)
        }
        coseHeaderKeys.forEach {
            cose.addProtectedAttribute(it.key, it.value)
        }
        cose.sign(signingService.getCborSigningKey())
        return cose.encode()
    }

    fun wrapWithSignature(content: ByteArray, coseHeaderKeys: Map<CoseHeaderKeys, Any> = mapOf()): SignedData {
        val signature = encodeSignature(content, coseHeaderKeys)
        return SignedData(content, signature)
    }


}
