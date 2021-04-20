package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.impl.VerificationCryptoService
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class CborProcessingChainNlTest {

    @Test
    fun pastInfected() {
        //  From https://github.com/ehn-digital-green-development/hcert-testdata/blob/main/testdata/test1.bin
        verify("5XK2F39WFMWU4F30\$BBOELJC+6BY50.FK NK\$QE6LCCEC-DDGEC3EF82B QE%JCZVCYNK4WE+6A7ECAED*96DL6WW6F:6YNK4WEL+97EC5EDNA74:6UW6UL6JPCT3E5JDNA7NW69463W5TG6K/E4QCB7L6VC\$QE-M8*+A%R81A6L44AIAMA8K/EIPCZEDZ C..DJ\$DSN8J\$D:KEIECPFFJECHWEH7A2FD5\$CN34 EDR+9Z C%8DLQE*CEZPC24EQD02VCCWENF6OF63W5KF6746GPCEVCWUC9C9Y 9JPC6\$C4\$EF1A B94W5:97- D8+9U441ECOPC1/DTPCBEC\$DD+3E*ED..DRWEGPC8%EB3ER.CDLFBJE H893E5UANPCYPC4\$CQD0HWE/TEJPCIEC6JD846Y969463W5/A62ECE\$D5IA/+A")
    }

    private fun verify(qrCodeContents: String) {
        val verificationResult = VerificationResult()

        val certificateRepository = PrefilledCertificateRepository()
        //TODO certificateRepository.addCertificate(kid, certificate)
        val decodingChain = buildChain(VerificationCryptoService(certificateRepository))

        val vaccinationData = decodingChain.verify(qrCodeContents, verificationResult)
        assertThat(verificationResult.coseVerified, equalTo(false))
        assertThat(vaccinationData.sub.dob, notNullValue())
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
