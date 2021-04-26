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
    fun success(input: String) {
        verify(input, RandomRsaKeyCryptoService(2048))
        verify(input, RandomRsaKeyCryptoService(3072))
        verify(input, RandomEcKeyCryptoService(256))
        verify(input, RandomEcKeyCryptoService(384))
        verify(input, RandomEcKeyCryptoService(521))
    }

    private fun verify(jsonInput: String, cryptoService: CryptoService) {
        val input = ObjectMapper().readValue(jsonInput, Eudgc::class.java)
        val verificationResult = VerificationResult()

        val encodingChain = Chain.buildCreationChain(cryptoService)
        val certificateRepository = PrefilledCertificateRepository(cryptoService.getCertificate())
        val decodingChain = Chain.buildVerificationChain(certificateRepository)

        val output = encodingChain.encode(input)

        val vaccinationData = decodingChain.decode(output.step5Prefixed, verificationResult)
        assertThat(vaccinationData, equalTo(input))
        assertThat(verificationResult.cborDecoded, equalTo(true))
    }

    companion object {

        @JvmStatic
        @Suppress("unused")
        fun stringProvider() = listOf(
            SampleData.recovery,
            SampleData.testRat,
            SampleData.testNaa,
            SampleData.vaccination
        )

    }

}
