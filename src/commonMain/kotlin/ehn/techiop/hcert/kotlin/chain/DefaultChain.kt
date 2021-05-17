package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.impl.*

object DefaultChain {

    fun buildCreationChain(cryptoService: CryptoService) = Chain(
        DefaultCborService(),
        DefaultCwtService(),
        DefaultCoseService.getInstance(cryptoService),
        DefaultContextIdentifierService(),
        DefaultCompressorService.getInstance(),
        DefaultBase45Service()
    )

    /**
     * Builds a "default" chain for verifying, i.e. one with the implementation according to spec.
     */
    fun buildVerificationChain(repository: CertificateRepository) = Chain(
        DefaultCborService(),
        DefaultCwtService(),
        VerificationCoseService(repository),
        DefaultContextIdentifierService(),
        DefaultCompressorService.getInstance(),
        DefaultBase45Service()
    )
}