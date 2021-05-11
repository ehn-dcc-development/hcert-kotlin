package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.data.Eudgc
import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCwtService
import ehn.techiop.hcert.kotlin.chain.impl.VerificationCoseService

/**
 * Main entry point for the creation/encoding and verification/decoding of HCERT data into QR codes
 *
 * @see [Eudgc]
 */
class Chain(
    private val cborService: CborService,
    private val cwtService: CwtService,
    private val coseService: CoseService,
    private val contextIdentifierService: ContextIdentifierService,
    private val compressorService: CompressorService,
    private val base45Service: Base45Service,
) {

    /**
     * Process the [input], apply encoding in this order:
     * - [CborService]
     * - [CwtService]
     * - [CoseService]
     * - [CompressorService]
     * - [Base45Service]
     * - [ContextIdentifierService]
     *
     * The result ([ChainResult]) will contain all intermediate steps, as well as the final result in [ChainResult.step5Prefixed].
     */
    fun encode(input: Eudgc): ChainResult {
        val cbor = cborService.encode(input)
        val cwt = cwtService.encode(cbor)
        val cose = coseService.encode(cwt)
        val compressed = compressorService.encode(cose)
        val encoded = base45Service.encode(compressed)
        val prefixedEncoded = contextIdentifierService.encode(encoded)
        return ChainResult(cbor, cwt, cose, compressed, encoded, prefixedEncoded)
    }

    /**
     * Process the [input], apply decoding in this order:
     * - [ContextIdentifierService]
     * - [Base45Service]
     * - [CompressorService]
     * - [CoseService]
     * - [CwtService]
     * - [CborService]
     * The result ([Eudgc]) will contain the parsed data.
     *
     * Beware that [verificationResult] will be filled with detailed information about the decoding,
     * which shall be passed to an instance of [DecisionService] to decide on a final verdict.
     */
    fun decode(input: String, verificationResult: VerificationResult): Eudgc {
        return decodeExtended(input, verificationResult).eudgc
    }

    /**
     * Process the [input], apply decoding in this order:
     * - [ContextIdentifierService]
     * - [Base45Service]
     * - [CompressorService]
     * - [CoseService]
     * - [CwtService]
     * - [CborService]
     * The result ([ChainDecodeResult]) will contain the parsed data, as well as intermediate results.
     *
     * Beware that [verificationResult] will be filled with detailed information about the decoding,
     * which shall be passed to an instance of [DecisionService] to decide on a final verdict.
     */
    fun decodeExtended(input: String, verificationResult: VerificationResult): ChainDecodeResult {
        val encoded = contextIdentifierService.decode(input, verificationResult)
        val compressed = base45Service.decode(encoded, verificationResult)
        val cose = compressorService.decode(compressed, verificationResult)
        val cwt = coseService.decode(cose, verificationResult)
        val cbor = cwtService.decode(cwt, verificationResult)
        val eudgc = cborService.decode(cbor, verificationResult)
        return ChainDecodeResult(eudgc, cbor, cwt, cose, compressed, encoded)
    }

    companion object {
        /**
         * Builds a "default" chain for encoding data, i.e. one with the implementation according to spec.
         */
        @JvmStatic
        fun buildCreationChain(cryptoService: CryptoService) = Chain(
            DefaultCborService(),
            DefaultCwtService(),
            DefaultCoseService(cryptoService),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            DefaultBase45Service()
        )

        /**
         * Builds a "default" chain for verifying, i.e. one with the implementation according to spec.
         */
        @JvmStatic
        fun buildVerificationChain(repository: CertificateRepository) = Chain(
            DefaultCborService(),
            DefaultCwtService(),
            VerificationCoseService(repository),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            DefaultBase45Service()
        )
    }

}

