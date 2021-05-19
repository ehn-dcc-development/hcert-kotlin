package ehn.techiop.hcert.kotlin.chain.ext

import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

expect fun loadResource(filename: String): String?

class ExtendedTestRunner {

    @Test
    fun verificationStarter() {
        verificationLoader("AT01.json")
        verificationLoader("AT02.json")
        verificationLoader("AT03.json")
        verificationLoader("AT04.json")
//        verificationLoader("HR01.json")
//        verificationLoader("HR02.json")
//        verificationLoader("HR03.json")
//        verificationLoader("SE01.json")
//        verificationLoader("SE02.json")
//        verificationLoader("SE03.json")
//        verificationLoader("SE04.json")
//        // TODO CBOR Tag? verificationLoader("SE05.json")
 verificationLoader("SE06.json")
//        verificationLoader("BG01.json")
//        verificationLoader("RO01.json")
//        verificationLoader("RO02.json")
        verificationLoader("IS01.json")
        verificationLoader("IS02.json")
//        verificationLoader("DK05.json")
//        verificationLoader("DK06.json")
//        verificationLoader("DK07.json")
//        verificationLoader("DK08.json")
//        verificationLoader("DK09.json")
        // TODO kid verificationLoader("GR01.json")
        // TODO kid verificationLoader("GR02.json")
        verificationLoader("testcaseQ1.json")
        //verificationLoader("testcaseQ2.json")
        verificationLoader("testcaseH1.json")
        verificationLoader("testcaseH2.json")
        verificationLoader("testcaseH3.json")
        verificationLoader("testcaseB1.json")
        verificationLoader("testcaseZ1.json")
        verificationLoader("testcaseZ2.json")
        //TODO RSA verificationLoader("testcaseCO1.json")
        //TODO RSA verificationLoader("testcaseCO2.json")
        verificationLoader("testcaseCO3.json")
        //verificationLoader("testcaseCO4.json")
        verificationLoader("testcaseCO5.json")
        verificationLoader("testcaseCO6.json")
        verificationLoader("testcaseCO7.json")
        verificationLoader("testcaseCO8.json")
        verificationLoader("testcaseCO9.json")
        verificationLoader("testcaseCO10.json")
        verificationLoader("testcaseCO11.json")
        verificationLoader("testcaseCO12.json")
        verificationLoader("testcaseCO13.json")
        verificationLoader("testcaseCO14.json")
        verificationLoader("testcaseCO15.json")
        verificationLoader("testcaseCO16.json")
        verificationLoader("testcaseCO17.json")
        verificationLoader("testcaseCO18.json")
        verificationLoader("testcaseCO19.json")
        verificationLoader("testcaseCO20.json")
        verificationLoader("testcaseCO21.json")
        // TODO: Who is responsible for verifying KID? verificationLoader("testcaseCO22.json")
        // TODO: Who is responsible for verifying KID? verificationLoader("testcaseCO23.json")
        verificationLoader("testcaseCBO1.json")
        verificationLoader("testcaseCBO2.json")
        verificationLoader("testcaseDGC1.json")
        verificationLoader("testcaseDGC2.json")
        verificationLoader("testcaseDGC3.json")
        verificationLoader("testcaseDGC4.json")
        verificationLoader("testcaseDGC5.json")
        verificationLoader("testcaseDGC6.json")
        verificationLoader("testcaseIlegalDateFormat.json")
    }

    fun verificationLoader(filename: String) {
        val loadResource = loadResource(filename)
        if (loadResource == null) {
            throw Throwable("Could not find resource $filename")
        }
        verification(filename, Json.decodeFromString(loadResource))

    }

    fun verification(filename: String, case: TestCase) {
        println("Executing verification test case \"${filename}\": \"${case.context.description}\"")
        val clock = case.context.validationClock?.let { FixedClock(it) } ?: Clock.System
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
            // TODO QRCode in Common and JS?
            //if (it) assertEquals(case.base45WithPrefix, qrCodeContent)
            //if (!it) assertEquals(VerificationDecision.FAIL, decision)
        }
        case.expectedResult.prefix?.let {
            if (it) assertEquals(case.base45, chainResult.step4Encoded, "Prefix Expected")
            if (!it) assertEquals(VerificationDecision.FAIL, decision, "Prefix Not Expected")
        }
        case.expectedResult.base45Decode?.let {
            assertEquals(it, verificationResult.base45Decoded, "Base45 Decoding Bin")
            if (it && case.compressedHex != null) {
                assertEquals(case.compressedHex.lowercase(), chainResult.step3Compressed.toHexString().lowercase(), "Base45 Decoding Hex")
            }
            if (!it) assertEquals(VerificationDecision.FAIL, decision, "Base54 Decoding Fail Expected")
        }
        case.expectedResult.compression?.let {
            assertEquals(it, verificationResult.zlibDecoded, "Zlib Decompression Bin")
            if (it) assertEquals(case.coseHex?.lowercase(), chainResult.step2Cose.toHexString().lowercase(), "Zlib Decompression Hex")
        }
        case.expectedResult.coseSignature?.let {
            assertEquals(it, verificationResult.coseVerified, "Cose Signature Verification")
            if (!it) assertEquals(VerificationDecision.FAIL, decision, "Cose Signature Verification FAIL Expected")
        }
        case.expectedResult.cborDecode?.let {
            assertEquals(it, verificationResult.cborDecoded, "CBOR Decoding")
            if (it) {
                assertEquals(case.eudgc, chainResult.eudgc, "CBOR Decoding GOOD Expected")
                // doesn't make sense to compare exact CBOR hex encoding
                //assertThat(chainResult.step1Cbor.toHexString(), equalToIgnoringCase(case.cborHex))
            }
            if (!it) assertEquals(VerificationDecision.FAIL, decision, "CBOR Decoding FAIL Expected")
        }
        case.expectedResult.json?.let {
            assertEquals(case.eudgc, chainResult.eudgc, "JSON Decoding")
            if (!it) assertEquals(VerificationDecision.FAIL, decision, "JSON Decoding FAIL expected")
        }
        case.expectedResult.schemaValidation?.let {
            // TODO Implement schema validation
            //assertThat(verificationResult.cborDecoded, equalTo(it))
            //if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
        }
        case.expectedResult.expirationCheck?.let {
            if (it) assertEquals(VerificationDecision.GOOD, decision, "Expiry Check GOOD Expected")
            if (!it) assertEquals(VerificationDecision.FAIL, decision, "Expiry Check FAIL Expected")
        }
        case.expectedResult.keyUsage?.let {
            if (it) assertEquals(VerificationDecision.GOOD, decision, "Key Usage GOOD Expected")
            if (!it) assertEquals(VerificationDecision.FAIL, decision, "Key Usage FAIL Expected")
        }
    }

}
