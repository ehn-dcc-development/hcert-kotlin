package ehn.techiop.hcert.kotlin.chain

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class CborProcessingChainNlTest {

    @Test
    fun pastInfected() {
        verify(
            "NCFOXN%TSMAHN-H5L486Q-LCBYUN+CWI47-5Y8EN6QBL53+LZEB\$ZJ*DJH75*84T*K.UKO KKFRV4C%47DK4V:6S16S45B.3A9J.6ANEBWD1UCIC2K%4HCW4C 1A CWHC2.9G58QWGNO37QQG UZ\$UBZP/BEMWIIOH%HMI*5O0I172Y5SX5Q.+HU1CQKQD1UACR96IDESM-FLX6WDDGAQZ1AUMJHE0ZKNL-K31J/7I*2VUWUE08NA9T141 LXRL QE4OB\$DVX A/DSU0AM361309JLU1"
        )
    }

    private fun verify(qrCodeContents: String) {
        val verificationResult = VerificationResult()

        val certificateRepository = PrefilledCertificateRepository()
        //TODO certificateRepository.addCertificate(kid, certificate)
        val decodingChain = buildChain(VerificationCryptoService(certificateRepository))

        val vaccinationData = decodingChain.verify(qrCodeContents, verificationResult)
        assertThat(verificationResult.coseVerified, equalTo(false))
        assertThat(vaccinationData.sub?.gen, notNullValue())
    }

    private fun buildChain(cryptoService: CryptoService): CborProcessingChain {
        val coseService = DefaultCoseService(cryptoService)
        val valSuiteService = DefaultValSuiteService()
        val compressorService = DefaultCompressorService()
        val base45Service = DefaultBase45Service()
        val cborService = DefaultCborService()
        return CborProcessingChain(cborService, coseService, valSuiteService, compressorService, base45Service)
    }

}
