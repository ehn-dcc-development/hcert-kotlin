package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class ChainNlTest {

    @Test
    fun pastInfected() {
        //  From https://github.com/ehn-digital-green-development/hcert-testdata/blob/main/testdata/test1.bin
        verify("5XK2F39WFMWU4F30\$BBOELJC+6BY50.FK NK\$QE6LCCEC-DDGEC3EF82B QE%JCZVCYNK4WE+6A7ECAED*96DL6WW6F:6YNK4WEL+97EC5EDNA74:6UW6UL6JPCT3E5JDNA7NW69463W5TG6K/E4QCB7L6VC\$QE-M8*+A%R81A6L44AIAMA8K/EIPCZEDZ C..DJ\$DSN8J\$D:KEIECPFFJECHWEH7A2FD5\$CN34 EDR+9Z C%8DLQE*CEZPC24EQD02VCCWENF6OF63W5KF6746GPCEVCWUC9C9Y 9JPC6\$C4\$EF1A B94W5:97- D8+9U441ECOPC1/DTPCBEC\$DD+3E*ED..DRWEGPC8%EB3ER.CDLFBJE H893E5UANPCYPC4\$CQD0HWE/TEJPCIEC6JD846Y969463W5/A62ECE\$D5IA/+A")
    }

    private fun verify(qrCodeContents: String) {
        val verificationResult = VerificationResult()

        val certificateRepository = PrefilledCertificateRepository(
            "\n" +
                    "-----BEGIN CERTIFICATE-----\n" +
                    "MIIBdTCCARoCCQDgC5Os3o4mgDAKBggqhkjOPQQDAjBCMQswCQYDVQQGEwJTRTER\n" +
                    "MA8GA1UECgwIS2lyZWkgQUIxDjAMBgNVBAsMBWhjZXJ0MRAwDgYDVQQDDAdpc3N1\n" +
                    "ZXIxMB4XDTIxMDMyNDEyMTE0M1oXDTIzMTIxOTEyMTE0M1owQjELMAkGA1UEBhMC\n" +
                    "U0UxETAPBgNVBAoMCEtpcmVpIEFCMQ4wDAYDVQQLDAVoY2VydDEQMA4GA1UEAwwH\n" +
                    "aXNzdWVyMTBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABKx5vnHbvvG0k3cyWIAZ\n" +
                    "bwbVK93XbxdAvDe/DtHwGMhi4NzjaPmOuU5HS1nNjL+/ge1FL4y9l7mqk11aC3BT\n" +
                    "jEswCgYIKoZIzj0EAwIDSQAwRgIhAKmgB/CTtrVeQ2Yh/ZK4nfQwtwIyLcKd3odL\n" +
                    "T5rAQADyAiEA/dRCuifyT1DENa09rdnnyzq3+uAEIzha7tRUvllZW/8=\n" +
                    "-----END CERTIFICATE-----"
        )
        val decodingChain = Chain.buildVerificationChain(certificateRepository)

        val vaccinationData = decodingChain.verify(qrCodeContents, verificationResult)
        assertThat(verificationResult.coseVerified, equalTo(false))
        assertThat(vaccinationData.sub.dob, notNullValue())
    }

}
