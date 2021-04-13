package ehn.techiop.hcert.kotlin.chain

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class CborProcessingChainTest {

    @Test
    fun pastInfected() {
        verify(SampleData.recovery, buildChain(RandomRsaKeyCryptoService()))
        verify(SampleData.recovery, buildChain(RandomEcKeyCryptoService()))
    }

    @Test
    fun tested() {
        verify(SampleData.test, buildChain(RandomRsaKeyCryptoService()))
        verify(SampleData.test, buildChain(RandomEcKeyCryptoService()))
    }

    @Test
    fun vaccinated() {
        verify(SampleData.vaccination, buildChain(RandomRsaKeyCryptoService()))
        verify(SampleData.vaccination, buildChain(RandomEcKeyCryptoService()))
    }

    private fun verify(s: String, cborProcessingChain: CborProcessingChain) {
        val input = Json { isLenient = true }.decodeFromString<VaccinationData>(s)
        val verificationResult = VerificationResult()

        val output = cborProcessingChain.process(input)

        val vaccinationData = cborProcessingChain.verify(output.prefixedEncodedCompressedCose, verificationResult)
        assertThat(vaccinationData, equalTo(input))
        assertThat(verificationResult.cborDecoded, equalTo(true))
    }

    private fun buildChain(cryptoService: CryptoService): CborProcessingChain {
        val coseService = DefaultCoseService(cryptoService)
        val valSuiteService = DefaultValSuiteService()
        val compressorService = CompressorService()
        val base45Service = Base45Service()
        val cborService = CborService()
        return CborProcessingChain(cborService, coseService, valSuiteService, compressorService, base45Service)
    }

}
