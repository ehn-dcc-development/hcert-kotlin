package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCwtService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultSchemaValidationService
import ehn.techiop.hcert.kotlin.chain.impl.VerificationCoseService
import kotlinx.datetime.Clock
import kotlin.js.JsName


object DefaultChain {
    @JsName("buildCreationChain")
    fun buildCreationChain(cryptoService: CryptoService) = Chain(
        DefaultCborService(),
        DefaultCwtService(),
        DefaultCoseService(cryptoService),
        DefaultContextIdentifierService(),
        DefaultCompressorService(),
        DefaultBase45Service(),
        DefaultSchemaValidationService()
    )

    /**
     * Builds a "default" chain for verifying, i.e. one with the implementation according to spec.
     */
    @JsName("buildVerificationChain")
    fun buildVerificationChain(repository: CertificateRepository, clock: Clock = Clock.System) = Chain(
        DefaultCborService(),
        DefaultCwtService(clock = clock),
        VerificationCoseService(repository),
        DefaultContextIdentifierService(),
        DefaultCompressorService(),
        DefaultBase45Service(),
        DefaultSchemaValidationService()
    )
}