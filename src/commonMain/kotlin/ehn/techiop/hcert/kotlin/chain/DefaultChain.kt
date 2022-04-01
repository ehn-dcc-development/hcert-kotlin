package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCwtService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultHigherOrderValidationService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultSchemaValidationService
import kotlinx.datetime.Clock
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


object DefaultChain {
    @JvmStatic
    @JsName("buildCreationChain")
    fun buildCreationChain(cryptoService: CryptoService, context: String = "HC1:") = Chain(
        DefaultHigherOrderValidationService(),
        DefaultSchemaValidationService(),
        DefaultCborService(),
        DefaultCwtService(),
        DefaultCoseService(cryptoService),
        DefaultCompressorService(),
        DefaultBase45Service(),
        DefaultContextIdentifierService(context)
    )

    /**
     * Builds a "default" chain for verifying, i.e. one with the implementation according to spec.
     */
    @JvmStatic
    @JvmOverloads
    @JsName("buildVerificationChain")
    fun buildVerificationChain(
        repository: CertificateRepository,
        atRepository: CertificateRepository? = null,
        clock: Clock = Clock.System
    ): IChain {
        val euContextService = DefaultContextIdentifierService("HC1:")
        val euChain = Chain(
            DefaultHigherOrderValidationService(),
            DefaultSchemaValidationService(),
            DefaultCborService(),
            DefaultCwtService(clock = clock),
            DefaultCoseService(repository),
            DefaultCompressorService(),
            DefaultBase45Service(),
            euContextService
        )
        if (atRepository == null)
            return euChain

        val atContextService = DefaultContextIdentifierService("AT1:")
        val atChain = Chain(
            DefaultHigherOrderValidationService(),
            DefaultSchemaValidationService(false, arrayOf("AT-1.0.0")),
            DefaultCborService(),
            DefaultCwtService(clock = clock),
            DefaultCoseService(atRepository),
            DefaultCompressorService(),
            DefaultBase45Service(),
            atContextService
        )

        return DelegatingChain(euChain, euContextService, atChain, atContextService)
    }
}