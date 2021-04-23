package ehn.techiop.hcert.kotlin.chain

import com.fasterxml.jackson.databind.ObjectMapper
import ehn.techiop.hcert.data.DigitalGreenCertificate
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.RandomRsaKeyCryptoService
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class ChainTest {

    @Test
    fun pastInfected() {
        verify(SampleData.recovery, RandomRsaKeyCryptoService(2048))
        verify(SampleData.recovery, RandomRsaKeyCryptoService(3072))
        verify(SampleData.recovery, RandomEcKeyCryptoService(256))
        verify(SampleData.recovery, RandomEcKeyCryptoService(384))
        verify(SampleData.recovery, RandomEcKeyCryptoService(521))
    }

    @Test
    fun tested() {
        verify(SampleData.test, RandomRsaKeyCryptoService(2048))
        verify(SampleData.test, RandomRsaKeyCryptoService(3072))
        verify(SampleData.test, RandomEcKeyCryptoService(256))
        verify(SampleData.test, RandomEcKeyCryptoService(384))
        verify(SampleData.test, RandomEcKeyCryptoService(521))
    }

    @Test
    fun vaccinated() {
        verify(SampleData.vaccination, RandomRsaKeyCryptoService(2048))
        verify(SampleData.vaccination, RandomRsaKeyCryptoService(3072))
        verify(SampleData.vaccination, RandomEcKeyCryptoService(256))
        verify(SampleData.vaccination, RandomEcKeyCryptoService(384))
        verify(SampleData.vaccination, RandomEcKeyCryptoService(521))
    }

    private fun verify(jsonInput: String, cryptoService: CryptoService) {
        val input = ObjectMapper().readValue(jsonInput, DigitalGreenCertificate::class.java)
        val verificationResult = VerificationResult()

        val encodingChain = Chain.buildCreationChain(cryptoService)
        val certificateRepository = PrefilledCertificateRepository(cryptoService.getCertificate())
        val decodingChain = Chain.buildVerificationChain(certificateRepository)

        val output = encodingChain.process(input)

        val vaccinationData = decodingChain.verify(output.prefixedEncodedCompressedCose, verificationResult)
        assertThat(vaccinationData, equalTo(input))
        assertThat(verificationResult.cborDecoded, equalTo(true))
    }

}
