package ehn.techiop.hcert.kotlin.chain

import com.fasterxml.jackson.databind.ObjectMapper
import ehn.techiop.hcert.data.Eudgc
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
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class FaultyImplementationsTest {

    private val cryptoService = RandomEcKeyCryptoService()
    private val cborService = DefaultCborService()
    private val coseService = DefaultCoseService(cryptoService)
    private val contextIdentifierService = DefaultContextIdentifierService()
    private val compressorService = DefaultCompressorService()
    private val base45Service = DefaultBase45Service()
    private val chainCorrect =
        Chain(cborService, coseService, contextIdentifierService, compressorService, base45Service)
    private val chainFaultyBase45 =
        Chain(
            cborService,
            coseService,
            contextIdentifierService,
            compressorService,
            FaultyBase45Service()
        )
    private val chainFaultyCompressor =
        Chain(
            cborService,
            coseService,
            contextIdentifierService,
            FaultyCompressorService(),
            base45Service
        )
    private val chainNoopCompressor =
        Chain(cborService, coseService, contextIdentifierService, NoopCompressorService(), base45Service)
    private val chainNoopContextIdentifier =
        Chain(
            cborService,
            coseService,
            NoopContextIdentifierService(),
            compressorService,
            base45Service
        )
    private val chainUnverifiableCose =
        Chain(
            cborService,
            NonVerifiableCoseService(cryptoService),
            contextIdentifierService,
            compressorService,
            base45Service
        )
    private val chainUnprotectedCose =
        Chain(
            cborService,
            UnprotectedCoseService(cryptoService),
            contextIdentifierService,
            compressorService,
            base45Service
        )
    private val chainFaultyCose =
        Chain(
            cborService,
            FaultyCoseService(cryptoService),
            contextIdentifierService,
            compressorService,
            base45Service
        )
    private val chainFaultyCbor =
        Chain(
            FaultyCborService(),
            coseService,
            contextIdentifierService,
            compressorService,
            base45Service
        )

    private val input = SampleData.vaccination
    private val decodedFromInput = ObjectMapper().readValue(input, Eudgc::class.java)

    @Test
    fun correct() {
        assertVerification(
            chainCorrect.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            true,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true; cborDecoded = true; coseVerified =
                true
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
                contextIdentifier = null; base45Decoded = true; zlibDecoded = true; coseVerified = true; cborDecoded =
                true
            })
    }

    @Test
    fun noopCompressor() {
        assertVerification(
            chainNoopCompressor.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            true,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = false; coseVerified =
                true; cborDecoded = true
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
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true; cborDecoded = true
            })
    }

    @Test
    fun unprotectedCose() {
        assertVerification(
            chainUnprotectedCose.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            true,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true; coseVerified = true; cborDecoded =
                true
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
    fun faultyCbor() {
        assertVerification(
            chainFaultyCbor.encode(decodedFromInput).step5Prefixed,
            decodedFromInput,
            false,
            VerificationResult().apply {
                contextIdentifier = "HC1:"; base45Decoded = true; zlibDecoded = true; coseVerified = true
            })
    }

    private fun assertVerification(
        chainOutput: String,
        input: Eudgc,
        expectDataToMatch: Boolean,
        expectedResult: VerificationResult
    ) {
        val verificationResult = VerificationResult()
        val vaccinationData = chainCorrect.decode(chainOutput, verificationResult)
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
