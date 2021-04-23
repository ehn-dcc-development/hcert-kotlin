package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.data.DigitalGreenCertificate
import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.impl.VerificationCryptoService


class Chain(
    private val cborService: CborService,
    private val coseService: CoseService,
    private val contextIdentifierService: ContextIdentifierService,
    private val compressorService: CompressorService,
    private val base45Service: Base45Service
) {

    fun process(input: DigitalGreenCertificate): ResultCbor {
        val cbor = cborService.encode(input)
        val cose = coseService.encode(cbor)
        val comCose = compressorService.encode(cose)
        val encodedComCose = base45Service.encode(comCose)
        val prefEncodedComCose = contextIdentifierService.encode(encodedComCose)
        return ResultCbor(cbor, cose, comCose, prefEncodedComCose)
    }

    fun verify(input: String, verificationResult: VerificationResult): DigitalGreenCertificate {
        val plainInput = contextIdentifierService.decode(input, verificationResult)
        val compressedCose = base45Service.decode(plainInput, verificationResult)
        val cose = compressorService.decode(compressedCose, verificationResult)
        val cbor = coseService.decode(cose, verificationResult)
        return cborService.decode(cbor, verificationResult)
    }

    companion object {
        @JvmStatic
        fun buildCreationChain(cryptoService: CryptoService) = Chain(
            DefaultCborService(),
            DefaultCoseService(cryptoService),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            DefaultBase45Service()
        )

        @JvmStatic
        fun buildVerificationChain(repository: CertificateRepository) = Chain(
            DefaultCborService(),
            DefaultCoseService(VerificationCryptoService(repository)),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            DefaultBase45Service()
        )
    }

}

data class ResultCbor(
    val cbor: ByteArray,
    val cose: ByteArray,
    val compressedCose: ByteArray,
    val prefixedEncodedCompressedCose: String
)

