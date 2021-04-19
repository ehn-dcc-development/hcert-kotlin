package ehn.techiop.hcert.kotlin.chain

import COSE.HeaderKeys
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import ehn.techiop.hcert.data.DigitalGreenCertificate
import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.RandomRsaKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.VerificationCryptoService
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class CborProcessingChainTest {

    @Test
    fun pastInfected() {
        verify(SampleData.recovery, RandomRsaKeyCryptoService())
        verify(SampleData.recovery, RandomEcKeyCryptoService())
    }

    @Test
    fun tested() {
        verify(SampleData.test, RandomRsaKeyCryptoService())
        verify(SampleData.test, RandomEcKeyCryptoService())
    }

    @Test
    fun vaccinated() {
        verify(SampleData.vaccination, RandomRsaKeyCryptoService())
        verify(SampleData.vaccination, RandomEcKeyCryptoService())
    }

    private fun verify(jsonInput: String, cryptoService: CryptoService) {
        val input = buildObjectMapper().readValue(jsonInput, DigitalGreenCertificate::class.java)
        val verificationResult = VerificationResult()

        val encodingChain = buildChain(cryptoService)
        val kid =
            cryptoService.getCborHeaders().first { it.first.AsCBOR() == HeaderKeys.KID.AsCBOR() }.second.GetByteString()
        val certificate = cryptoService.getCertificate(kid)
        val certificateRepository = PrefilledCertificateRepository()
        certificateRepository.addCertificate(kid, certificate)
        val decodingChain = buildChain(VerificationCryptoService(certificateRepository))

        val output = encodingChain.process(input)

        val vaccinationData = decodingChain.verify(output.prefixedEncodedCompressedCose, verificationResult)
        assertThat(vaccinationData, equalTo(input))
        assertThat(verificationResult.cborDecoded, equalTo(true))
    }

    private fun buildObjectMapper() = ObjectMapper().apply { registerModule(JavaTimeModule()) }

    private fun buildChain(cryptoService: CryptoService): CborProcessingChain {
        val coseService = DefaultCoseService(cryptoService)
        val contextIdentifierService = DefaultContextIdentifierService()
        val compressorService = DefaultCompressorService()
        val base45Service = DefaultBase45Service()
        val cborService = DefaultCborService()
        return CborProcessingChain(cborService, coseService, contextIdentifierService, compressorService, base45Service)
    }

}
