package ehn.techiop.hcert.kotlin.chain


import ehn.techiop.hcert.kotlin.chain.faults.*
import ehn.techiop.hcert.kotlin.chain.impl.*
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test

class FaultyImplementationsTest {

    private val cryptoService = RandomEcKeyCryptoService()
    private val cborService = DefaultCborService()
    private val cwtService = DefaultCwtService()
    private val coseService = DefaultCoseService(cryptoService)
    private val contextIdentifierService = DefaultContextIdentifierService()
    private val compressorService = DefaultCompressorService()
    private val base45Service = DefaultBase45Service()
    private val schemaValidationService= DefaultSchemaValidationService()
    private val chainCorrect =
        Chain(cborService, cwtService, coseService, contextIdentifierService, compressorService, base45Service, schemaValidationService)
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
        Chain(cborService, cwtService, coseService, contextIdentifierService, NoopCompressorService(), base45Service, schemaValidationService)
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

    @Test
    fun correct() {
        assertVerification(
            chainCorrect.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            true,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true; coseVerified = true; cwtDecoded =
                true; cborDecoded = true
            })
    }

    @Test
    fun faultyBase45() {
        assertVerification(
            chainFaultyBase45.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            false,
            VerificationResult().apply { contextIdentifier = "HC1:" })
    }

    @Test
    fun noopContext() {
        assertVerification(
            chainNoopContextIdentifier.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            true,
            VerificationResult().apply {
                contextIdentifier = null; base45Decoded = true; zlibDecoded = true; coseVerified = true; cwtDecoded =
                true; cborDecoded = true
            })
    }

    @Test
    fun noopCompressor() {
        assertVerification(
            chainNoopCompressor.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            false,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true;
            })
    }

    @Test
    fun faultyCompressor() {
        assertVerification(
            chainFaultyCompressor.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            false,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = false
            })
    }

    @Test
    fun unverifiableCose() {
        assertVerification(
            chainUnverifiableCose.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            true,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true; cwtDecoded = true; cborDecoded =
                true
            })
    }

    @Test
    fun unprotectedCose() {
        assertVerification(
            chainUnprotectedCose.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            true,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true; coseVerified = true; cwtDecoded =
                true; cborDecoded = true
            })
    }

    @Test
    fun faultyCose() {
        assertVerification(
            chainFaultyCose.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            false,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true
            })
    }

    @Test
    fun faultyCwt() {
        assertVerification(
            chainFaultyCwt.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            false,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true; coseVerified = true
            })
    }

    @Test
    fun faultyCbor() {
        assertVerification(
            chainFaultyCbor.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            false,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true; coseVerified = true; cwtDecoded =
                true
            })
    }

    private fun assertVerification(
        chainOutput: String,
        input: GreenCertificate,
        expectDataToMatch: Boolean,
        expectedResult: VerificationResult,
    ) {
        val vaccinationData = chainCorrect.decode(chainOutput)
        val verificationResult =vaccinationData.verificationResult
        MatcherAssert.assertThat(verificationResult.base45Decoded, CoreMatchers.equalTo(expectedResult.base45Decoded))
        MatcherAssert.assertThat(verificationResult.cwtDecoded, CoreMatchers.equalTo(expectedResult.cwtDecoded))
        MatcherAssert.assertThat(verificationResult.cborDecoded, CoreMatchers.equalTo(expectedResult.cborDecoded))
        MatcherAssert.assertThat(verificationResult.coseVerified, CoreMatchers.equalTo(expectedResult.coseVerified))
        MatcherAssert.assertThat(verificationResult.zlibDecoded, CoreMatchers.equalTo(expectedResult.zlibDecoded))
        MatcherAssert.assertThat(
            verificationResult.contextIdentifier,
            CoreMatchers.equalTo(expectedResult.contextIdentifier)
        )
        if (expectDataToMatch) {
            MatcherAssert.assertThat(vaccinationData.greenCertificate, CoreMatchers.equalTo(input))
        } else {
            MatcherAssert.assertThat(vaccinationData.greenCertificate, CoreMatchers.not(CoreMatchers.equalTo(input)))
        }
    }

}