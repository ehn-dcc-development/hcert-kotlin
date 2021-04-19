package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.faults.FaultyBase45Service
import ehn.techiop.hcert.kotlin.chain.faults.FaultyCborService
import ehn.techiop.hcert.kotlin.chain.faults.FaultyCompressorService
import ehn.techiop.hcert.kotlin.chain.faults.FaultyCoseService
import ehn.techiop.hcert.kotlin.chain.faults.NonVerifiableCoseService
import ehn.techiop.hcert.kotlin.chain.faults.NoopCompressorService
import ehn.techiop.hcert.kotlin.chain.faults.NoopContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.faults.UnprotectedCoseService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class TestSuiteTests {

    private val cryptoService = RandomEcKeyCryptoService()
    private val cborService = DefaultCborService()
    private val coseService = DefaultCoseService(cryptoService)
    private val contextIdentifierService = DefaultContextIdentifierService()
    private val compressorService = DefaultCompressorService()
    private val base45Service = DefaultBase45Service()
    private val chainCorrect =
        CborProcessingChain(cborService, coseService, contextIdentifierService, compressorService, base45Service)
    private val chainFaultyBase45 =
        CborProcessingChain(
            cborService,
            coseService,
            contextIdentifierService,
            compressorService,
            FaultyBase45Service()
        )
    private val chainFaultyCompressor =
        CborProcessingChain(
            cborService,
            coseService,
            contextIdentifierService,
            FaultyCompressorService(),
            base45Service
        )
    private val chainNoopCompressor =
        CborProcessingChain(cborService, coseService, contextIdentifierService, NoopCompressorService(), base45Service)
    private val chainNoopContextIdentifier =
        CborProcessingChain(
            cborService,
            coseService,
            NoopContextIdentifierService(),
            compressorService,
            base45Service
        )
    private val chainUnverifiableCose =
        CborProcessingChain(
            cborService,
            NonVerifiableCoseService(cryptoService),
            contextIdentifierService,
            compressorService,
            base45Service
        )
    private val chainUnprotectedCose =
        CborProcessingChain(
            cborService,
            UnprotectedCoseService(cryptoService),
            contextIdentifierService,
            compressorService,
            base45Service
        )
    private val chainFaultyCose =
        CborProcessingChain(
            cborService,
            FaultyCoseService(cryptoService),
            contextIdentifierService,
            compressorService,
            base45Service
        )
    private val chainFaultyCbor =
        CborProcessingChain(
            FaultyCborService(),
            coseService,
            contextIdentifierService,
            compressorService,
            base45Service
        )


    @Test
    fun vaccination() {
        val input = SampleData.vaccination
        val decodedFromInput =
            Json { isLenient = true; ignoreUnknownKeys = true }.decodeFromString<VaccinationData>(input)

        assertVerification(
            chainCorrect.process(decodedFromInput).prefixedEncodedCompressedCose,
            decodedFromInput,
            true,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true; cborDecoded = true; coseVerified =
                true
            })
        assertVerification(
            chainFaultyBase45.process(decodedFromInput).prefixedEncodedCompressedCose,
            decodedFromInput,
            false,
            VerificationResult().apply { contextIdentifier = "HC1:" })
        assertVerification(
            chainNoopContextIdentifier.process(decodedFromInput).prefixedEncodedCompressedCose,
            decodedFromInput,
            true,
            VerificationResult().apply {
                contextIdentifier = null; base45Decoded = true; zlibDecoded = true; cborDecoded = true; coseVerified =
                true
            })
        assertVerification(
            chainNoopCompressor.process(decodedFromInput).prefixedEncodedCompressedCose,
            decodedFromInput,
            true,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = false; cborDecoded =
                true; coseVerified =
                true
            })
        assertVerification(
            chainFaultyCompressor.process(decodedFromInput).prefixedEncodedCompressedCose,
            decodedFromInput,
            false,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = false
            })
        assertVerification(
            chainUnverifiableCose.process(decodedFromInput).prefixedEncodedCompressedCose,
            decodedFromInput,
            true,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true; cborDecoded = true
            })
        assertVerification(
            chainUnprotectedCose.process(decodedFromInput).prefixedEncodedCompressedCose,
            decodedFromInput,
            true,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true; coseVerified = true; cborDecoded =
                true
            })
        assertVerification(
            chainFaultyCose.process(decodedFromInput).prefixedEncodedCompressedCose,
            decodedFromInput,
            false,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true
            })
        assertVerification(
            chainFaultyCbor.process(decodedFromInput).prefixedEncodedCompressedCose,
            decodedFromInput,
            false,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true; coseVerified = true
            })
    }

    private fun assertVerification(
        chainOutput: String,
        input: VaccinationData,
        expectDataToMatch: Boolean,
        expectedResult: VerificationResult
    ) {
        val verificationResult = VerificationResult()
        val vaccinationData = chainCorrect.verify(chainOutput, verificationResult)
        assertThat(verificationResult.base45Decoded, equalTo(expectedResult.base45Decoded))
        assertThat(verificationResult.cborDecoded, equalTo(expectedResult.cborDecoded))
        assertThat(verificationResult.coseVerified, equalTo(expectedResult.coseVerified))
        assertThat(verificationResult.zlibDecoded, equalTo(expectedResult.zlibDecoded))
        assertThat(verificationResult.contextIdentifier, equalTo(expectedResult.contextIdentifier))
        if (expectDataToMatch) {
            assertThat(vaccinationData, equalTo(input))
        } else {
            assertThat(vaccinationData, not(equalTo(input)))
        }
    }

}
