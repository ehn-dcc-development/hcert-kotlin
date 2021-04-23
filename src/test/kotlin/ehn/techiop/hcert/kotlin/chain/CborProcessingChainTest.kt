package ehn.techiop.hcert.kotlin.chain

import com.fasterxml.jackson.databind.ObjectMapper
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

        val encodingChain = buildChain(cryptoService)
        val certificateRepository = buildPrefilledCertificateRepo(cryptoService)
        val decodingChain = buildChain(VerificationCryptoService(certificateRepository))

        val output = encodingChain.process(input)

        val vaccinationData = decodingChain.verify(output.prefixedEncodedCompressedCose, verificationResult)
        assertThat(vaccinationData, equalTo(input))
        assertThat(verificationResult.cborDecoded, equalTo(true))
    }

    private fun buildPrefilledCertificateRepo(cryptoService: CryptoService): PrefilledCertificateRepository {
        val (kid, certificate) = cryptoService.getCertificate()
        val certificateRepository = PrefilledCertificateRepository()
        certificateRepository.addCertificate(kid, certificate)
        return certificateRepository
    }

    private fun buildChain(cryptoService: CryptoService): CborProcessingChain {
        val coseService = DefaultCoseService(cryptoService)
        val contextIdentifierService = DefaultContextIdentifierService()
        val compressorService = DefaultCompressorService()
        val base45Service = DefaultBase45Service()
        val cborService = DefaultCborService()
        return CborProcessingChain(cborService, coseService, contextIdentifierService, compressorService, base45Service)
    }

}
