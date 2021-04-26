package ehn.techiop.hcert.kotlin.chain

import com.fasterxml.jackson.databind.ObjectMapper
import ehn.techiop.hcert.data.Eudgc
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.RandomRsaKeyCryptoService
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class ChainTest {

    @ParameterizedTest
    @MethodSource("stringProvider")
    fun successRsa(input: TestInput) {
        verify(input.data, RandomRsaKeyCryptoService(2048, listOf(input.certType)), input.outcome)
        verify(input.data, RandomRsaKeyCryptoService(3072, listOf(input.certType)), input.outcome)
    }

    @ParameterizedTest
    @MethodSource("stringProvider")
    fun successEc(input: TestInput) {
        verify(input.data, RandomEcKeyCryptoService(256, listOf(input.certType)), input.outcome)
        verify(input.data, RandomEcKeyCryptoService(384, listOf(input.certType)), input.outcome)
    }

    private fun verify(jsonInput: String, cryptoService: CryptoService, outcome: VerificationDecision) {
        val input = ObjectMapper().readValue(jsonInput, Eudgc::class.java)
        val verificationResult = VerificationResult()

        val encodingChain = Chain.buildCreationChain(cryptoService)
        val certificateRepository = PrefilledCertificateRepository(cryptoService.getCertificate())
        val decodingChain = Chain.buildVerificationChain(certificateRepository)

        val output = encodingChain.encode(input)

        val vaccinationData = decodingChain.decode(output.step5Prefixed, verificationResult)
        assertThat(vaccinationData, equalTo(input))
        assertThat(verificationResult.cborDecoded, equalTo(true))
        println(verificationResult)
        assertThat(DecisionService().decide(verificationResult), equalTo(outcome))
    }

    companion object {

        @JvmStatic
        @Suppress("unused")
        fun stringProvider() = listOf(
            TestInput(SampleData.testRat, CertType.TEST, VerificationDecision.GOOD),
            TestInput(SampleData.testRat, CertType.VACCINATION, VerificationDecision.FAIL),
            TestInput(SampleData.testNaa, CertType.TEST, VerificationDecision.GOOD),
            TestInput(SampleData.testNaa, CertType.RECOVERY, VerificationDecision.FAIL),
            TestInput(SampleData.vaccination, CertType.VACCINATION, VerificationDecision.GOOD),
            TestInput(SampleData.vaccination, CertType.TEST, VerificationDecision.FAIL),
            TestInput(SampleData.recovery, CertType.RECOVERY, VerificationDecision.GOOD),
            TestInput(SampleData.recovery, CertType.VACCINATION, VerificationDecision.FAIL),
        )

    }

    data class TestInput(val data: String, val certType: CertType, val outcome: VerificationDecision)

}
