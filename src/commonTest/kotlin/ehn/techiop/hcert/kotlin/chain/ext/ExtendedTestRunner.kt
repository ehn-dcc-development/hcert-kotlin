package ehn.techiop.hcert.kotlin.chain.ext

import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

expect fun allOfficialTestCases(): Map<String, String>

class ExtendedTestRunner {

    @Test
    fun verificationStarter() {
        allOfficialTestCases()
            .filterNot { it.key.contains("NL/") } // https://github.com/eu-digital-green-certificates/dgc-testdata/issues/107
            .filterNot { it.key.contains("FR/") } // https://github.com/eu-digital-green-certificates/dgc-testdata/issues/128
            .filterNot { it.key.contains("CY/") } // https://github.com/eu-digital-green-certificates/dgc-testdata/issues/105
            .filterNot { it.key.contains("DE/") } // https://github.com/eu-digital-green-certificates/dgc-testdata/issues/119
            .filterNot { it.key.contains("ES/") } // https://github.com/eu-digital-green-certificates/dgc-testdata/issues/32
            .filterNot { it.key.contains("IS/") } //TODO DateTime Parsing (JVM too)
            .filterNot { it.key.contains("PL/") } //TODO Expirationcheck (JVM too)
            .filterNot { it.key.contains("SE/") } //TODO Cose Tags (JVM too)
            .filterNot { it.key.contains("SI/") } //TODO Cose Tags (JVM too)
            .filterNot { it.key.contains("IT/") } //TODO DateTimeParseException: Text '2021-05-17T18:22:17' could not be parsed at index 19: 2021-05-17T18:22:17, at index: 19 -- only JS!
            .filterNot { it.key.contains("BG/") } //TODO COSE verification failed -- only JS!
            .filterNot { it.key.contains("LU/") } //TODO COSE verification failed -- only JS!
            .filterNot { it.key.contains("RO/2DCode/raw/4.json") } //TODO CBOR decoding failed (JVM too)
            //.filterNot { it.key.contains("CO1.json") } //TODO RSA failed -- only JS!
            //.filterNot { it.key.contains("CO2.json") } //TODO RSA failed -- only JS!
            .forEach {
                val case = Json { ignoreUnknownKeys = true }.decodeFromString<TestCase>(it.value)
                verification(it.key, case)
            }
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
                assertEquals(
                    case.compressedHex.lowercase(),
                    chainResult.step3Compressed.toHexString().lowercase(),
                    "Base45 Decoding Hex"
                )
            }
            if (!it) assertEquals(VerificationDecision.FAIL, decision, "Base54 Decoding Fail Expected")
        }
        case.expectedResult.compression?.let {
            assertEquals(it, verificationResult.zlibDecoded, "Zlib Decompression Bin")
            if (it) assertEquals(
                case.coseHex?.lowercase(),
                chainResult.step2Cose.toHexString().lowercase(),
                "Zlib Decompression Hex"
            )
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
            assertEquals(it, verificationResult.schemaValidated)
            if (!it) assertEquals(VerificationDecision.FAIL, decision, "Schema Validation FAIL expected")
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
