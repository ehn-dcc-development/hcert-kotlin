package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.impl.*
import ehn.techiop.hcert.kotlin.data.GreenCertificate
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