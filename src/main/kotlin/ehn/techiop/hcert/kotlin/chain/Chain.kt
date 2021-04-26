package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.data.DigitalGreenCertificate
import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.impl.VerificationCryptoService

/**
 * Main entry point for the creation and verification of HCERT
 *
 * @see [DigitalGreenCertificate]
 */
class Chain(
    private val cborService: CborService,
    private val coseService: CoseService,
    private val contextIdentifierService: ContextIdentifierService,
    private val compressorService: CompressorService,
    private val base45Service: Base45Service
) {

    /**
     * Process the [input], apply encoding from [CborService], [CoseService], [CompressorService], [Base45Service] and [ContextIdentifierService] (in that order). The [ChainResult] will contain all intermediate steps, as well as the final result in [ChainResult.step5Prefixed].
     */
    fun encode(input: DigitalGreenCertificate): ChainResult {
        val cbor = cborService.encode(input)
        val cose = coseService.encode(cbor)
        val compressed = compressorService.encode(cose)
        val encoded = base45Service.encode(compressed)
        val prefixedEncoded = contextIdentifierService.encode(encoded)
        return ChainResult(cbor, cose, compressed, encoded, prefixedEncoded)
    }

    /**
     * Process the [input], apply decoding from [ContextIdentifierService], [Base45Service], [CompressorService], [CoseService], [CborService] (in that order). The result will contain the parsed data.
     *
     * Beware that [verificationResult] will be filled with detailed information about the decoding, which shall be passed to [DecisionService] to decide on a final verdict.
     */
    fun decode(input: String, verificationResult: VerificationResult): DigitalGreenCertificate {
        val plainInput = contextIdentifierService.decode(input, verificationResult)
        val compressedCose = base45Service.decode(plainInput, verificationResult)
        val cose = compressorService.decode(compressedCose, verificationResult)
        val cbor = coseService.decode(cose, verificationResult)
        return cborService.decode(cbor, verificationResult)
    }

    companion object {
        @JvmStatic
        fun buildCreationChain(cryptoService: CryptoService) = Chain(
            DefaultCborService(),
            DefaultCoseService(cryptoService),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            DefaultBase45Service()
        )

        @JvmStatic
        fun buildVerificationChain(repository: CertificateRepository) = Chain(
            DefaultCborService(),
            DefaultCoseService(VerificationCryptoService(repository)),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            DefaultBase45Service()
        )
    }

}

