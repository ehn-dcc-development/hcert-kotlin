package ehn.techiop.hcert.kotlin.chain

import COSE.HeaderKeys
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class CborProcessingChainNlTest {

    @Test
    fun pastInfected() {
        verify(
            "NCFOXN%TSMAHN-H5L486Q-LCBYUN+CWI47-5Y8EN6QBL53+LZEB\$ZJ*DJH75*84T*K.UKO KKFRV4C%47DK4V:6S16S45B.3A9J.6ANEBWD1UCIC2K%4HCW4C 1A CWHC2.9G58QWGNO37QQG UZ\$UBZP/BEMWIIOH%HMI*5O0I172Y5SX5Q.+HU1CQKQD1UACR96IDESM-FLX6WDDGAQZ1AUMJHE0ZKNL-K31J/7I*2VUWUE08NA9T141 LXRL QE4OB\$DVX A/DSU0AM361309JLU1",
            RandomRsaKeyCryptoService()
        )
    }

    private fun verify(qrCodeContents: String, cryptoService: CryptoService) {
        val verificationResult = VerificationResult()

        val kid =
            cryptoService.getCborHeaders().first { it.first.AsCBOR() == HeaderKeys.KID.AsCBOR() }.second.AsString()
        val certificate = cryptoService.getCertificate(kid)
        val certificateRepository = RemoteCachedCertificateRepository("doesntmatter")
        certificateRepository.addCertificate(kid, certificate)
        val decodingChain = buildChain(VerificationCryptoService(certificateRepository))

        val vaccinationData = decodingChain.verify(qrCodeContents, verificationResult)
        assertThat(verificationResult.cborDecoded, equalTo(true))
    }

    private fun buildChain(cryptoService: CryptoService): CborProcessingChain {
        val coseService = LenientCoseService(cryptoService)
        val valSuiteService = LenientValSuiteService()
        val compressorService = CompressorService()
        val base45Service = Base45Service()
        val cborService = CborService()
        return CborProcessingChain(cborService, coseService, valSuiteService, compressorService, base45Service)
    }

}
