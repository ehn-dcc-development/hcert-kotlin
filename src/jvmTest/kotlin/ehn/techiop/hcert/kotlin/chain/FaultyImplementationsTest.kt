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
            true,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true; coseVerified = true; cwtDecoded =
                true; cborDecoded = true
            })
    }

    "Faulty Base45"{
        assertVerification(
            chainFaultyBase45.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            false,
            VerificationResult().apply { contextIdentifier = "HC1:" })
    }

    "NOOP Context"{
        assertVerification(
            chainNoopContextIdentifier.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            true,
            VerificationResult().apply {
                contextIdentifier = null; base45Decoded = true; zlibDecoded = true; coseVerified = true; cwtDecoded =
                true; cborDecoded = true
            })
    }

    "NOOP Compressor" {
        assertVerification(
            chainNoopCompressor.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            false,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true;
            })
    }

    "Faulty Compressor"{
        assertVerification(
            chainFaultyCompressor.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            false,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = false
            })
    }

    "Unverifiable COSE" {
        assertVerification(
            chainUnverifiableCose.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            false,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true
            })
    }

    "Unprotected COSE" {
        assertVerification(
            chainUnprotectedCose.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            true,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true; coseVerified = true; cwtDecoded =
                true; cborDecoded = true
            })
    }

    "Faulty COSE" {
        assertVerification(
            chainFaultyCose.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            false,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true
            })
    }

    "Faulty CWT"{
        assertVerification(
            chainFaultyCwt.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            false,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true; coseVerified = true
            })
    }

    "Faulty CBOR"{
        assertVerification(
            chainFaultyCbor.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            false,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true; coseVerified = true; cwtDecoded =
                true
            })
    }


})

private fun assertVerification(
    chainOutput: String,
    input: GreenCertificate,
    expectDataToMatch: Boolean,
    expectedResult: VerificationResult,
) {
    val result = chainCorrect.decode(chainOutput)
    val verificationResult = result.verificationResult
    verificationResult.base45Decoded shouldBe expectedResult.base45Decoded
    verificationResult.cwtDecoded shouldBe expectedResult.cwtDecoded
    verificationResult.cborDecoded shouldBe expectedResult.cborDecoded
    verificationResult.coseVerified shouldBe expectedResult.coseVerified
    verificationResult.zlibDecoded shouldBe expectedResult.zlibDecoded
    verificationResult.contextIdentifier shouldBe expectedResult.contextIdentifier

    if (expectDataToMatch) {
        result.greenCertificate shouldBe input
    } else {
        result.greenCertificate shouldNotBe input
    }
}
