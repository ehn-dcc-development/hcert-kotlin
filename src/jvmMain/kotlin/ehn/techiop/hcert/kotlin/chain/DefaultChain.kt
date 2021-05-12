package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.impl.*

object DefaultChain {
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