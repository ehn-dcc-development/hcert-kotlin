package ehn.techiop.hcert.kotlin.chain.debug

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.Chain
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.DefaultChain
import ehn.techiop.hcert.kotlin.chain.impl.*
import kotlinx.datetime.Clock
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


object DebugChain{
    @JvmStatic
    @JsName("buildCreationChain")
    fun buildCreationChain(cryptoService: CryptoService) = DefaultChain.buildCreationChain(cryptoService)

    /**
     * Builds a "default" chain for verifying, i.e. one with the implementation according to spec.
     */
    @JvmStatic
    @JvmOverloads
    @JsName("buildVerificationChain")
    fun buildVerificationChain(repository: CertificateRepository, clock: Clock = Clock.System) = Chain(
        DebugHigherOrderValidationService(),
        DebugSchemaValidationService(),
        DefaultCborService(),
        DebugCwtService(clock = clock),
        DebugCoseService(repository),
        DefaultCompressorService(),
        DefaultBase45Service(),
        DebugContextIdentifierService()
    )
}