package ehn.techiop.hcert.kotlin.rules

import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import ehn.techiop.hcert.kotlin.trust.SignedData
import ehn.techiop.hcert.kotlin.trust.SignedDataEncodeService
import kotlinx.datetime.Clock
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import kotlin.jvm.JvmOverloads
import kotlin.time.Duration


/**
 * Encodes a list of business rules as [SignedData] object
 */
class BusinessRulesV1EncodeService constructor(
    signingService: CryptoService,
    validity: Duration = Duration.hours(48),
    clock: Clock = Clock.System,
) {

    @JvmOverloads
    constructor(signingService: CryptoService, validityHours: Int = 48)
            : this(signingService, Duration.hours(validityHours), Clock.System)

    private val signedDataService = SignedDataEncodeService(signingService, validity, clock)

    /**
     * Content is a CBOR encoded [BusinessRulesContainer] object, i.e. a list of business rules
     */
    private fun encodeContent(input: List<BusinessRule>): ByteArray {
        val content = BusinessRulesContainer(input.toTypedArray())
        return Cbor.encodeToByteArray(content)
    }

    /**
     * See [SignedData] for details about returned structure
     */
    fun encode(input: List<BusinessRule>): SignedData {
        val content = encodeContent(input)
        val headers = mapOf(CoseHeaderKeys.BUSINESS_RULES_VERSION to 1)
        return signedDataService.wrapWithSignature(content, headers)
    }
}
