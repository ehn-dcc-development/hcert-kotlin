package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.impl.*
import kotlinx.datetime.Clock
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


object DefaultChain {
    @JvmStatic
    @JsName("buildCreationChain")
    fun buildCreationChain(cryptoService: CryptoService) = Chain(
        DefaultHigherOrderValidationService(),
        DefaultSchemaValidationService(),
        DefaultCborService(),
        DefaultCwtService(),
        DefaultCoseService(cryptoService),
        DefaultCompressorService(),
        DefaultBase45Service(),
        DefaultContextIdentifierService()
    )

    /**
     * Builds a "default" chain for verifying, i.e. one with the implementation according to spec.
     */
    @JvmStatic
    @JvmOverloads
    @JsName("buildVerificationChain")
    fun buildVerificationChain(repository: CertificateRepository, clock: Clock = Clock.System) = Chain(
        DefaultHigherOrderValidationService(),
        DefaultSchemaValidationService(),
        DefaultCborService(),
        DefaultCwtService(clock = clock),
        DefaultCoseService(repository),
        DefaultCompressorService(),
        DefaultBase45Service(),
        DefaultContextIdentifierService()
    )
}