package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.crypto.CoseHeaderKeys
import kotlinx.datetime.Clock
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import kotlin.time.Duration


/**
 * Encodes a list of business rules as [SignedData] object
 */
class BusinessRulesV1EncodeService constructor(
    signingService: CryptoService,
    validity: Duration = Duration.hours(48),
    clock: Clock = Clock.System,
) {

    private val signedDataService = SignedDataEncodeService(signingService, validity, clock)

    /**
     * Content is a CBOR encoded [BusinessRulesContainer] object, i.e. a list of business rules
     */
    private fun encodeContent(input: List<BusinessRule>): ByteArray {
        val content = BusinessRulesContainer(input)
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
