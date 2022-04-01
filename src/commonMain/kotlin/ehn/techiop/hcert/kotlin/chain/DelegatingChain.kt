package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.data.GreenCertificate

/**
 * Can decode incoming HCERT data depending on Context, e.g. either "HC1:" or "AT1:"
 */
class DelegatingChain(
    private val euChain: Chain,
    private val euContextService: ContextIdentifierService,
    private val atChain: Chain,
    private val atContextService: ContextIdentifierService
) : IChain {

    override fun encode(input: GreenCertificate): ChainResult {
        return euChain.encode(input)
    }

    override fun decode(input: String): DecodeResult {
        val check = VerificationResult()
        return try {
            euContextService.decode(input, check)
            euChain.decode(input)
        } catch (_: VerificationException) {
            try {
                atContextService.decode(input, check)
                atChain.decode(input)
            } catch (e: VerificationException) {
                DecodeResult(
                    VerificationResult().apply { error = e.error;e.details?.let { errorDetails.putAll(it) } },
                    ChainDecodeResult(listOf(e.error), null, null, null, null, null, null)
                )
            }
        }
    }
}