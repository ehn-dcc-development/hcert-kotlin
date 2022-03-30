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
        val euChain = Chain(
            DefaultHigherOrderValidationService(),
            DefaultSchemaValidationService(),
            DefaultCborService(),
            DefaultCwtService(clock = clock),
            DefaultCoseService(repository),
            DefaultCompressorService(),
            DefaultBase45Service(),
            DefaultContextIdentifierService("HC1:")
        )
        if (atRepository == null)
            return euChain

        val atChain = Chain(
            DefaultHigherOrderValidationService(),
            DefaultSchemaValidationService(false, arrayOf("AT-1.0.0")),
            DefaultCborService(),
            DefaultCwtService(clock = clock),
            DefaultCoseService(atRepository),
            DefaultCompressorService(),
            DefaultBase45Service(),
            DefaultContextIdentifierService("AT1:")
        )

        return object : IChain {
            override fun encode(input: GreenCertificate): ChainResult {
                return euChain.encode(input)
            }

            override fun decode(input: String) = when {
                input.startsWith("AT1:") -> atChain.decode(input)
                input.startsWith("HC1:") -> euChain.decode(input)
                else -> DecodeResult(
                    VerificationResult(),
                    ChainDecodeResult(listOf(Error.INVALID_SCHEME_PREFIX), null, null, null, null, null, null)
                )
            }

        }
    }
}