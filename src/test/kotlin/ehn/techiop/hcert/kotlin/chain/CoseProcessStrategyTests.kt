package ehn.techiop.hcert.kotlin.chain

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class CoseProcessStrategyTests {

    private val cryptoService = RandomKeyCryptoService()
    private val cborService = CborService(cryptoService)
    private val valSuiteService = ValSuiteService()
    private val compressorService = CompressorService()
    private val base45Service = Base45Service()
    private val cborProcessingChain =
        CborProcessingChain(cborService, valSuiteService, compressorService, base45Service)

    @Test
    fun pastInfected() {
        val input = Json { isLenient = true }.decodeFromString<VaccinationData>(Input.pastInfectedJson)

        val output = cborProcessingChain.process(input)

        val vaccinationData = cborProcessingChain.verify(output.prefixedEncodedCompressedCose)
        assertThat(vaccinationData, equalTo(input))
    }

    @Test
    fun tested() {
        val input = Json { isLenient = true }.decodeFromString<VaccinationData>(Input.testedJson)

        val output = cborProcessingChain.process(input)

        val vaccinationData = cborProcessingChain.verify(output.prefixedEncodedCompressedCose)
        assertThat(vaccinationData, equalTo(input))
    }

    @Test
    fun vaccinated() {
        val input = Json { isLenient = true }.decodeFromString<VaccinationData>(Input.vaccinatedJson)

        val output = cborProcessingChain.process(input)

        val vaccinationData = cborProcessingChain.verify(output.prefixedEncodedCompressedCose)
        assertThat(vaccinationData, equalTo(input))
    }

}
