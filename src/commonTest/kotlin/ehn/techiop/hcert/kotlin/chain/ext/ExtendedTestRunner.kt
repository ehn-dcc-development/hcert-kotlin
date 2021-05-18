package ehn.techiop.hcert.kotlin.chain.ext

import ehn.techiop.hcert.kotlin.chain.DecisionService
import ehn.techiop.hcert.kotlin.chain.DefaultChain
import ehn.techiop.hcert.kotlin.chain.VerificationDecision
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.toHexString
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class ExtendedTestRunner {

    @Test
    fun verificationStarter() {
        verificationLoader("src/commonTest/resources/AT01.json")
        verificationLoader("src/commonTest/resources/AT02.json")
        verificationLoader("src/commonTest/resources/AT03.json")
        verificationLoader("src/commonTest/resources/AT04.json")
        verificationLoader("src/commonTest/resources/HR01.json")
        verificationLoader("src/commonTest/resources/HR02.json")
        verificationLoader("src/commonTest/resources/HR03.json")
        verificationLoader("src/commonTest/resources/SE01.json")
        verificationLoader("src/commonTest/resources/SE02.json")
        verificationLoader("src/commonTest/resources/SE03.json")
        verificationLoader("src/commonTest/resources/SE04.json")
        // TODO CBOR Tag? verificationLoader("src/commonTest/resources/SE05.json")
        // TODO Validity? verificationLoader("src/commonTest/resources/SE06.json")
        verificationLoader("src/commonTest/resources/BG01.json")
        verificationLoader("src/commonTest/resources/RO01.json")
        verificationLoader("src/commonTest/resources/RO02.json")
        // TODO Datetime verificationLoader("src/commonTest/resources/IS01.json")
        // TODO Datetime verificationLoader("src/commonTest/resources/IS02.json")
        verificationLoader("src/commonTest/resources/DK05.json")
        verificationLoader("src/commonTest/resources/DK06.json")
        verificationLoader("src/commonTest/resources/DK07.json")
        verificationLoader("src/commonTest/resources/DK08.json")
        verificationLoader("src/commonTest/resources/DK09.json")
        // TODO kid verificationLoader("src/commonTest/resources/GR01.json")
        // TODO kid verificationLoader("src/commonTest/resources/GR02.json")
        verificationLoader("src/commonTest/resources/testcaseQ1.json")
        //verificationLoader("src/commonTest/resources/testcaseQ2.json")
        verificationLoader("src/commonTest/resources/testcaseH1.json")
        verificationLoader("src/commonTest/resources/testcaseH2.json")
        verificationLoader("src/commonTest/resources/testcaseH3.json")
        verificationLoader("src/commonTest/resources/testcaseB1.json")
        verificationLoader("src/commonTest/resources/testcaseZ1.json")
        verificationLoader("src/commonTest/resources/testcaseZ2.json")
        verificationLoader("src/commonTest/resources/testcaseCO1.json")
        verificationLoader("src/commonTest/resources/testcaseCO2.json")
        verificationLoader("src/commonTest/resources/testcaseCO3.json")
        //verificationLoader("src/commonTest/resources/testcaseCO4.json")
        verificationLoader("src/commonTest/resources/testcaseCO5.json")
        verificationLoader("src/commonTest/resources/testcaseCO6.json")
        verificationLoader("src/commonTest/resources/testcaseCO7.json")
        verificationLoader("src/commonTest/resources/testcaseCO8.json")
        verificationLoader("src/commonTest/resources/testcaseCO9.json")
        verificationLoader("src/commonTest/resources/testcaseCO10.json")
        verificationLoader("src/commonTest/resources/testcaseCO11.json")
        verificationLoader("src/commonTest/resources/testcaseCO12.json")
        verificationLoader("src/commonTest/resources/testcaseCO13.json")
        verificationLoader("src/commonTest/resources/testcaseCO14.json")
        verificationLoader("src/commonTest/resources/testcaseCO15.json")
        verificationLoader("src/commonTest/resources/testcaseCO16.json")
        verificationLoader("src/commonTest/resources/testcaseCO17.json")
        verificationLoader("src/commonTest/resources/testcaseCO18.json")
        verificationLoader("src/commonTest/resources/testcaseCO19.json")
        verificationLoader("src/commonTest/resources/testcaseCO20.json")
        verificationLoader("src/commonTest/resources/testcaseCO21.json")
        verificationLoader("src/commonTest/resources/testcaseCO22.json")
        verificationLoader("src/commonTest/resources/testcaseCO23.json")
        verificationLoader("src/commonTest/resources/testcaseCBO1.json")
        verificationLoader("src/commonTest/resources/testcaseCBO2.json")
        verificationLoader("src/commonTest/resources/testcaseDGC1.json")
        verificationLoader("src/commonTest/resources/testcaseDGC2.json")
        verificationLoader("src/commonTest/resources/testcaseDGC3.json")
        verificationLoader("src/commonTest/resources/testcaseDGC4.json")
        verificationLoader("src/commonTest/resources/testcaseDGC5.json")
        verificationLoader("src/commonTest/resources/testcaseDGC6.json")
    }

    fun verificationLoader(filename: String) {
        val contents = "" // todo load file
        val context = TestContext(
            1,
            "1.0.0",
            certificate = null,
            validationClock = Clock.System.now(),
            description = "foo"
        )
        val expectedResult = TestExpectedResults()
        val case = TestCase(context = context, expectedResult = expectedResult)
        verification(filename, case)
    }

    fun verification(filename: String, case: TestCase) {
        println("Executing verification test case \"${filename}\": \"${case.context.description}\"")
        // TODO val clock = case.context.validationClock?.let { Clock.fixed(it, ZoneOffset.UTC) } ?: Clock.systemUTC()
        val clock = case.context.validationClock?.let { Clock.System } ?: Clock.System
        val decisionService = DecisionService(clock)
        if (case.context.certificate == null) throw IllegalArgumentException("certificate")
        val certificateRepository = PrefilledCertificateRepository(case.context.certificate)
        val verificationResult = VerificationResult()
        val decodingChain = DefaultChain.buildVerificationChain(certificateRepository)
        val qrCodeContent =
            case.base45WithPrefix
                ?: if (case.qrCodePng != null) {
                    try {
                        // TODO decode QRCode?
                        //DefaultTwoDimCodeService(350).decode(case.qrCodePng.fromBase64())
                        case.qrCodePng
                    } catch (e: Exception) {
                        case.expectedResult.qrDecode?.let {
                            if (it) throw e
                        }
                        throw e
                    }
                } else throw IllegalArgumentException("Input")

        val chainResult = decodingChain.decodeExtended(qrCodeContent, verificationResult)
        val decision = decisionService.decide(verificationResult)

        case.expectedResult.qrDecode?.let {
            if (it) assertEquals(case.base45WithPrefix, qrCodeContent)
            if (!it) assertEquals(VerificationDecision.FAIL, decision)
        }
        case.expectedResult.prefix?.let {
            if (it) assertEquals(case.base45, chainResult.step4Encoded)
            if (!it) assertEquals(VerificationDecision.FAIL, decision)
        }
        case.expectedResult.base45Decode?.let {
            assertEquals(it, verificationResult.base45Decoded)
            if (it && case.compressedHex != null) {
                assertEquals(case.compressedHex.lowercase(), chainResult.step3Compressed.toHexString().lowercase())
            }
            if (!it) assertEquals(VerificationDecision.FAIL, decision)
        }
        case.expectedResult.compression?.let {
            assertEquals(it, verificationResult.zlibDecoded)
            if (it) assertEquals(case.coseHex?.lowercase(), chainResult.step2Cose.toHexString().lowercase())
        }
        case.expectedResult.coseSignature?.let {
            assertEquals(it, verificationResult.coseVerified)
            if (!it) assertEquals(VerificationDecision.FAIL, decision)
        }
        case.expectedResult.cborDecode?.let {
            assertEquals(it, verificationResult.cborDecoded)
            if (it) {
                assertEquals(case.eudgc, chainResult.eudgc)
                // doesn't make sense to compare exact CBOR hex encoding
                //assertThat(chainResult.step1Cbor.toHexString(), equalToIgnoringCase(case.cborHex))
            }
            if (!it) assertEquals(VerificationDecision.FAIL, decision)
        }
        case.expectedResult.json?.let {
            assertEquals(case.eudgc, chainResult.eudgc)
            if (!it) assertEquals(VerificationDecision.FAIL, decision)
        }
        case.expectedResult.schemaValidation?.let {
            // TODO Implement schema validation
            //assertThat(verificationResult.cborDecoded, equalTo(it))
            //if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
        }
        case.expectedResult.expirationCheck?.let {
            if (it) assertEquals(VerificationDecision.GOOD, decision)
            if (!it) assertEquals(VerificationDecision.FAIL, decision)
        }
        case.expectedResult.keyUsage?.let {
            if (it) assertEquals(VerificationDecision.GOOD, decision)
            if (!it) assertEquals(VerificationDecision.FAIL, decision)
        }
    }

}
