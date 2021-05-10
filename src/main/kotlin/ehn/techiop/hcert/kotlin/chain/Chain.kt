package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.data.Eudgc
import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCwtService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.impl.VerificationCoseService

/**
 * Main entry point for the creation and verification of HCERT
 *
 * @see [Eudgc]
 */
class Chain(
    private val cwtService: CwtService,
    private val coseService: CoseService,
    private val contextIdentifierService: ContextIdentifierService,
    private val compressorService: CompressorService,
    private val base45Service: Base45Service
) {

    /**
     * Process the [input], apply encoding from [CwtService], [CoseService], [CompressorService], [Base45Service] and [ContextIdentifierService] (in that order). The [ChainResult] will contain all intermediate steps, as well as the final result in [ChainResult.step5Prefixed].
     */
    fun encode(input: Eudgc): ChainResult {
        val cwt = cwtService.encode(input)
        val cose = coseService.encode(cwt)
        val compressed = compressorService.encode(cose)
        val encoded = base45Service.encode(compressed)
        val prefixedEncoded = contextIdentifierService.encode(encoded)
        return ChainResult(cwt, cose, compressed, encoded, prefixedEncoded)
    }

    /**
     * Process the [input], apply decoding from [ContextIdentifierService], [Base45Service], [CompressorService], [CoseService], [CwtService] (in that order). The result will contain the parsed data.
     *
     * Beware that [verificationResult] will be filled with detailed information about the decoding, which shall be passed to [DecisionService] to decide on a final verdict.
     */
    fun decode(input: String, verificationResult: VerificationResult): Eudgc {
        return decodeExtended(input, verificationResult).eudgc
    }

    /**
     * Process the [input], apply decoding from [ContextIdentifierService], [Base45Service], [CompressorService], [CoseService], [CwtService] (in that order).
     * The result will contain the parsed data, as well as intermediate results.
     *
     * Beware that [verificationResult] will be filled with detailed information about the decoding, which shall be passed to [DecisionService] to decide on a final verdict.
     */
    fun decodeExtended(input: String, verificationResult: VerificationResult): ChainDecodeResult {
        val encoded = contextIdentifierService.decode(input, verificationResult)
        val compressed = base45Service.decode(encoded, verificationResult)
        val cose = compressorService.decode(compressed, verificationResult)
        val cbor = coseService.decode(cose, verificationResult)
        val eudgc = cwtService.decode(cbor, verificationResult)
        return ChainDecodeResult(eudgc, cbor, cose, compressed, encoded)
    }

    companion object {
        @JvmStatic
        fun buildCreationChain(cryptoService: CryptoService) = Chain(
            DefaultCwtService(),
            DefaultCoseService(cryptoService),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            DefaultBase45Service()
        )

        @JvmStatic
        fun buildVerificationChain(repository: CertificateRepository) = Chain(
            DefaultCwtService(),
            VerificationCoseService(repository),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            DefaultBase45Service()
        )
    }

}

