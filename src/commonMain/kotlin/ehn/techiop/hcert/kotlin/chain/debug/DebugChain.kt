package ehn.techiop.hcert.kotlin.chain.debug

import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.data.GreenCertificate
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

        return object : IChain {
            override fun encode(input: GreenCertificate): ChainResult {
                return euChain.encode(input)
            }

            override fun decode(input: String): DecodeResult {
                val check = VerificationResult()
                return try {
                    euContextService.decode(input, check)
                    euChain.decode(input)
                } catch (_: VerificationException) {
                    try {
                        atContextService.decode(input, check)
                        atChain.decode(input)
                    } catch (e: VerificationException) {
                        DecodeResult(
                            VerificationResult().apply { error = e.error;e.details?.let { errorDetails.putAll(it) } },
                            ChainDecodeResult(listOf(e.error), null, null, null, null, null, null)
                        )
                    }
                }
            }
        }
    }
}