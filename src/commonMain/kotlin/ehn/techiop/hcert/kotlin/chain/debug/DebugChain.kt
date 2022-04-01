package ehn.techiop.hcert.kotlin.chain.debug

import ehn.techiop.hcert.kotlin.chain.CertificateRepository
import ehn.techiop.hcert.kotlin.chain.Chain
import ehn.techiop.hcert.kotlin.chain.DelegatingChain
import ehn.techiop.hcert.kotlin.chain.IChain
import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import kotlinx.datetime.Clock
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


object DebugChain {

    /**
     * Builds a "default" chain for verifying, i.e. one with the implementation according to spec.
     */
    @JvmStatic
    @JvmOverloads
    @JsName("buildVerificationChain")
    fun buildVerificationChain(
        repository: CertificateRepository,
        clock: Clock = Clock.System,
        atRepository: CertificateRepository? = null
    ): IChain {
        val euContextService = DebugContextIdentifierService("HC1:")
        val euChain = Chain(
            DebugHigherOrderValidationService(),
            DebugSchemaValidationService(),
            DefaultCborService(),
            DebugCwtService(clock = clock),
            DebugCoseService(repository),
            DefaultCompressorService(),
            DefaultBase45Service(),
            euContextService
        )
        if (atRepository == null)
            return euChain

        val atContextService = DebugContextIdentifierService("AT1:")
        val atChain = Chain(
            DebugHigherOrderValidationService(),
            DebugSchemaValidationService(false, arrayOf("AT-1.0.0")),
            DefaultCborService(),
            DebugCwtService(clock = clock),
            DebugCoseService(atRepository),
            DefaultCompressorService(),
            DefaultBase45Service(),
            atContextService
        )

        return DelegatingChain(euChain, euContextService, atChain, atContextService)
    }
}

