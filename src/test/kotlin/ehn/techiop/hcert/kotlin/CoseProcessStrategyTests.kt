package ehn.techiop.hcert.kotlin

import com.google.zxing.BarcodeFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.lessThan
import org.junit.jupiter.api.Test

class CoseProcessStrategyTests {

    private val qrCodeService = TwoDimCodeService(350, BarcodeFormat.QR_CODE)
    private val aztecService = TwoDimCodeService(350, BarcodeFormat.AZTEC)
    private val cryptoService = RandomKeyCryptoService()
    private val cborService = CborService(cryptoService)
    private val valSuiteService = ValSuiteService()
    private val compressorService = CompressorService()
    private val base45Service = Base45Service()
    private val cborProcessingChain = CborProcessingChain(cborService, valSuiteService, compressorService, base45Service)
    private val cborViewAdapter = CborViewAdapter(cborProcessingChain, base45Service, qrCodeService, aztecService)

    @Test
    fun pastInfected() {
        val cardViewModel = cborViewAdapter.process(Input.pastInfectedJson)

        assertThat(cardViewModel.title, equalTo("COSE"))
        assertThat(cardViewModel.base64Items.find { it.title == "CBOR (Base45)" }?.value?.length, isAround(408))
        assertThat(cardViewModel.base64Items.find { it.title == "COSE (Base45)" }?.value?.length, isAround(543))

        val prefixedCompressedCose = cardViewModel.base64Items.find { it.title == "Prefixed Compressed COSE" }?.value
        assertThat(prefixedCompressedCose?.length, isAround(535))
        if (prefixedCompressedCose == null) throw AssertionError()
        assertPlain(prefixedCompressedCose, Input.pastInfectedJson)
    }

    @Test
    fun vaccinated() {
        val cardViewModel = cborViewAdapter.process(Input.vaccinatedJson)

        assertThat(cardViewModel.title, equalTo("COSE"))
        assertThat(cardViewModel.base64Items.find { it.title == "CBOR (Base45)" }?.value?.length, isAround(840))
        assertThat(cardViewModel.base64Items.find { it.title == "COSE (Base45)" }?.value?.length, isAround(975))

        val prefixedCompressedCose = cardViewModel.base64Items.find { it.title == "Prefixed Compressed COSE" }?.value
        assertThat(prefixedCompressedCose?.length, isAround(718))
        if (prefixedCompressedCose == null) throw AssertionError()
        assertPlain(prefixedCompressedCose, Input.vaccinatedJson)
    }

    @Test
    fun tested() {
        val cardViewModel = cborViewAdapter.process(Input.testedJson)

        assertThat(cardViewModel.title, equalTo("COSE"))
        assertThat(cardViewModel.base64Items.find { it.title == "CBOR (Base45)" }?.value?.length, isAround(750))
        assertThat(cardViewModel.base64Items.find { it.title == "COSE (Base45)" }?.value?.length, isAround(885))

        val prefixedCompressedCose = cardViewModel.base64Items.find { it.title == "Prefixed Compressed COSE" }?.value
        assertThat(prefixedCompressedCose?.length, isAround(778))
        if (prefixedCompressedCose == null) throw AssertionError()
        assertPlain(prefixedCompressedCose, Input.testedJson)
    }

    private fun isAround(input: Int) = allOf(greaterThan(input.div(10) * 9), lessThan(input.div(10) * 11))

    private fun assertPlain(input: String, jsonInput: String) {
        val plainInput = valSuiteService.decode(input)
        val comCose = base45Service.decode(plainInput)
        val cose = compressorService.decode(comCose)
        val cbor = cborService.verify(cose)
        val vaccinationData = cborService.decode<VaccinationData>(cbor)
        val decodedFromInput = Json { isLenient = true }.decodeFromString<VaccinationData>(jsonInput)
        assertThat(vaccinationData, equalTo(decodedFromInput))
    }

}
