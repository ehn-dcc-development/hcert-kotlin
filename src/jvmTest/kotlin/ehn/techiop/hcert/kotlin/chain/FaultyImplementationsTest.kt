package ehn.techiop.hcert.kotlin.chain


import ehn.techiop.hcert.kotlin.chain.faults.*
import ehn.techiop.hcert.kotlin.chain.impl.*
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


private val cryptoService = RandomEcKeyCryptoService()
private val cborService = DefaultCborService()
private val cwtService = DefaultCwtService()
private val coseService = DefaultCoseService(cryptoService)
private val contextIdentifierService = DefaultContextIdentifierService()
private val compressorService = DefaultCompressorService()
private val base45Service = DefaultBase45Service()
private val schemaValidationService = DefaultSchemaValidationService()
private val higherOrderValidationService = DefaultHigherOrderValidationService()
private val chainCorrect =
    Chain(
        higherOrderValidationService,
        schemaValidationService,
        cborService,
        cwtService,
        coseService,
        compressorService,
        base45Service,
        contextIdentifierService
    )
private val chainFaultyBase45 =
    Chain(
        higherOrderValidationService,
        schemaValidationService,
        cborService,
        cwtService,
        coseService,
        compressorService,
        FaultyBase45Service(),
        contextIdentifierService
    )
private val chainFaultyCompressor =
    Chain(
        higherOrderValidationService,
        schemaValidationService,
        cborService,
        cwtService,
        coseService,
        FaultyCompressorService(),
        base45Service,
        contextIdentifierService
    )
private val chainNoopCompressor =
    Chain(
        higherOrderValidationService,
        schemaValidationService,
        cborService,
        cwtService,
        coseService,
        NoopCompressorService(),
        base45Service,
        contextIdentifierService
    )
private val chainNoopContextIdentifier =
    Chain(
        higherOrderValidationService,
        schemaValidationService,
        cborService,
        cwtService,
        coseService,
        compressorService,
        base45Service,
        NoopContextIdentifierService()
    )
private val chainUnverifiableCose =
    Chain(
        higherOrderValidationService,
        schemaValidationService,
        cborService,
        cwtService,
        NonVerifiableCoseService(cryptoService),
        compressorService,
        base45Service,
        contextIdentifierService
    )
private val chainUnprotectedCose =
    Chain(
        higherOrderValidationService,
        schemaValidationService,
        cborService,
        cwtService,
        UnprotectedCoseService(cryptoService),
        compressorService,
        base45Service,
        contextIdentifierService
    )
private val chainFaultyCose =
    Chain(
        higherOrderValidationService,
        schemaValidationService,
        cborService,
        cwtService,
        FaultyCoseService(cryptoService),
        compressorService,
        base45Service,
        contextIdentifierService
    )
private val chainFaultyCwt =
    Chain(
        higherOrderValidationService,
        schemaValidationService,
        cborService,
        FaultyCwtService(),
        coseService,
        compressorService,
        base45Service,
        contextIdentifierService
    )
private val chainFaultyCbor =
    Chain(
        higherOrderValidationService,
        schemaValidationService,
        FaultyCborService(),
        cwtService,
        coseService,
        compressorService,
        base45Service,
        contextIdentifierService
    )

private val input = SampleData.vaccination
private val decodedFromInput = Json.decodeFromString<GreenCertificate>(input)

class FaultyImplementationsTest : StringSpec({


    "correct" {
        assertVerification(
            chainCorrect.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
        )
    }

    "Faulty Base45"{
        assertVerification(
            chainFaultyBase45.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            Error.BASE_45_DECODING_FAILED
        )
    }

    "NOOP Context"{
        assertVerification(
            chainNoopContextIdentifier.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            Error.INVALID_SCHEME_PREFIX
        )
    }

    "NOOP Compressor" {
        assertVerification(
            chainNoopCompressor.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            Error.DECOMPRESSION_FAILED
        )
    }

    "Faulty Compressor"{
        assertVerification(
            chainFaultyCompressor.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            Error.DECOMPRESSION_FAILED
        )
    }

    "Unverifiable COSE" {
        assertVerification(
            chainUnverifiableCose.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            Error.SIGNATURE_INVALID
        )
    }

    "Unprotected COSE" {
        assertVerification(
            chainUnprotectedCose.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            null
        )
    }

    "Faulty COSE" {
        assertVerification(
            chainFaultyCose.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            Error.SIGNATURE_INVALID
        )
    }

    "Faulty CWT"{
        assertVerification(
            chainFaultyCwt.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            Error.CBOR_DESERIALIZATION_FAILED
        )
    }

    "Faulty CBOR"{
        assertVerification(
            chainFaultyCbor.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            Error.CBOR_DESERIALIZATION_FAILED
        )
    }


})

private fun assertVerification(
    chainOutput: String,
    input: GreenCertificate,
    error: Error? = null
) {
    val result = chainCorrect.decode(chainOutput)
    val verificationResult = result.verificationResult
    if (error != null) {
        result.chainDecodeResult.eudgc shouldNotBe input
        verificationResult.error shouldBe error
    } else {
        result.chainDecodeResult.eudgc shouldBe input
    }
}
