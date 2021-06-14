package ehn.techiop.hcert.kotlin.chain


import ehn.techiop.hcert.kotlin.chain.faults.FaultyBase45Service
import ehn.techiop.hcert.kotlin.chain.faults.FaultyCborService
import ehn.techiop.hcert.kotlin.chain.faults.FaultyCompressorService
import ehn.techiop.hcert.kotlin.chain.faults.FaultyCoseService
import ehn.techiop.hcert.kotlin.chain.faults.FaultyCwtService
import ehn.techiop.hcert.kotlin.chain.faults.NonVerifiableCoseService
import ehn.techiop.hcert.kotlin.chain.faults.NoopCompressorService
import ehn.techiop.hcert.kotlin.chain.faults.NoopContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.faults.UnprotectedCoseService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCwtService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultSchemaValidationService
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
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
private val chainCorrect =
    Chain(
        cborService,
        cwtService,
        coseService,
        contextIdentifierService,
        compressorService,
        base45Service,
        schemaValidationService
    )
private val chainFaultyBase45 =
    Chain(
        cborService,
        cwtService,
        coseService,
        contextIdentifierService,
        compressorService,
        FaultyBase45Service(), schemaValidationService
    )
private val chainFaultyCompressor =
    Chain(
        cborService,
        cwtService,
        coseService,
        contextIdentifierService,
        FaultyCompressorService(),
        base45Service, schemaValidationService
    )
private val chainNoopCompressor =
    Chain(
        cborService,
        cwtService,
        coseService,
        contextIdentifierService,
        NoopCompressorService(),
        base45Service,
        schemaValidationService
    )
private val chainNoopContextIdentifier =
    Chain(
        cborService,
        cwtService,
        coseService,
        NoopContextIdentifierService(),
        compressorService,
        base45Service, schemaValidationService
    )
private val chainUnverifiableCose =
    Chain(
        cborService,
        cwtService,
        NonVerifiableCoseService(cryptoService),
        contextIdentifierService,
        compressorService,
        base45Service, schemaValidationService
    )
private val chainUnprotectedCose =
    Chain(
        cborService,
        cwtService,
        UnprotectedCoseService(cryptoService),
        contextIdentifierService,
        compressorService,
        base45Service, schemaValidationService
    )
private val chainFaultyCose =
    Chain(
        cborService,
        cwtService,
        FaultyCoseService(cryptoService),
        contextIdentifierService,
        compressorService,
        base45Service, schemaValidationService
    )
private val chainFaultyCwt =
    Chain(
        cborService,
        FaultyCwtService(),
        coseService,
        contextIdentifierService,
        compressorService,
        base45Service, schemaValidationService
    )
private val chainFaultyCbor =
    Chain(
        FaultyCborService(),
        cwtService,
        coseService,
        contextIdentifierService,
        compressorService,
        base45Service, schemaValidationService
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
            Error.SCHEMA_VALIDATION_FAILED
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
