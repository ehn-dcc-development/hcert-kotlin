package ehn.techiop.hcert.kotlin.chain.ext

import ehn.techiop.hcert.kotlin.chain.DecisionService
import ehn.techiop.hcert.kotlin.chain.DefaultChain
import ehn.techiop.hcert.kotlin.chain.VerificationDecision
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.fromHexString
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultSchemaValidationService
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.toHexString
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

expect fun allOfficialTestCases(): Map<String, String>
private val json = Json { ignoreUnknownKeys = true }
private fun Map<String, String>.workaroundKotestNamingBug() =
    this.map { (k, v) -> Pair(k.replace(".json", "·json"), v) }.toMap()


class CommonTests : ExtendedTestRunner(allOfficialTestCases()
    .filter { it.key.contains("common/") }
    .filterNot { it.key.contains("DGC1") } // TODO Schema validation
    .filterNot { it.key.contains("DGC2") } // TODO Schema validation
    .filterNot { it.key.contains("CO28") } // TODO JS COSE
    .workaroundKotestNamingBug())

class MemberstateTests : ExtendedTestRunner(allOfficialTestCases()
    .filterNot { it.key.contains("common/") }
    .filterNot { it.key.contains("CY/") } // com.google.zxing.ChecksumException
    .filterNot { it.key.contains("CZ/") } // Version not an int
    .filterNot { it.key.contains("DE/") } // Issued At not right
    .filterNot { it.key.contains("test+recovery") } // Certificate missing OID
    .filterNot { it.key.contains("recovery+vaccination") } // Certificate missing OID
    .filterNot { it.key.contains("test+vaccination") } // Certificate missing OID
    .filterNot { it.key.contains("+wrong") } // Certificate missing OID
    .filterNot { it.key.contains("ES/2DCode/raw/1101") } // Wrong value set entry "729999"
    .filterNot { it.key.contains("ES/2DCode/raw/1103") } // Wrong value set entry "94558-4"
    .filterNot { it.key.contains("IT/2DCode/raw/4") } // Wrong value set entry <empty>
    .filterNot { it.key.contains("LV/2DCode/raw/2") } // Wrong value set entry "1drop Inc"
    .filterNot { it.key.contains("BE/2DCode/raw/3") } // Schema not correct for 1.2.1
    .filterNot { it.key.contains("FR/2DCode/raw/test_pcr_ok") } // Schema not correct for 1.2.1
    .filterNot { it.key.contains("NL/") } // Schema not correct, wrong value set entries
    .filterNot { it.key.contains("IS/2DCode/raw/3") } // Wrong test on key usage
    .filterNot { it.key.contains("PL/2DCode/raw/7") } // TODO JVM Schema Validation
    .filterNot { it.key.contains("PL/2DCode/raw/8") } // TODO JVM Schema Validation
    .filterNot { it.key.contains("PL/2DCode/raw/9") } // TODO JVM Schema Validation
    .filterNot { it.key.contains("PT/") } // TODO Empty/null arrays
    .filterNot { it.key.contains("SE/2DCode/raw/5") } // TODO JS COSE Tags CWT and Sign1
    .filterNot { it.key.contains("SE/2DCode/raw/7") } // TODO CBOR Tag C0
    .filterNot { it.key.contains("SI/2DCode/raw/5") } // TODO JS COSE Tags CWT and Sign1
    .filterNot { it.key.contains("BG/2DCode/raw/1") } // TODO JS COSE
    .filterNot { it.key.contains("ES/2DCode/raw/1501") } // TODO JS COSE
    .filterNot { it.key.contains("ES/2DCode/raw/1502") } // TODO JS COSE
    .filterNot { it.key.contains("ES/2DCode/raw/1503") } // TODO JS COSE
    .filterNot { it.key.contains("ES/2DCode/raw/401") } // TODO JS COSE
    .filterNot { it.key.contains("ES/2DCode/raw/402") } // TODO JS COSE
    .filterNot { it.key.contains("ES/2DCode/raw/403") } // TODO JS COSE
    .filterNot { it.key.contains("ES/2DCode/raw/701") } // TODO JS COSE
    .filterNot { it.key.contains("ES/2DCode/raw/702") } // TODO JS COSE
    .filterNot { it.key.contains("ES/2DCode/raw/703") } // TODO JS COSE
    .filterNot { it.key.contains("LU/2DCode/raw/1") } // TODO JS COSE
    .filterNot { it.key.contains("SE/2DCode/raw/3") } // TODO JS COSE
    .filterNot { it.key.contains("SE/2DCode/raw/4") } // TODO JS COSE
    .filterNot { it.key.contains("SI/2DCode/raw/3") } // TODO JS COSE
    .filterNot { it.key.contains("SI/2DCode/raw/4") } // TODO JS COSE
    //.filter { it.key.contains("SE/2DCode/raw/6") }
    .workaroundKotestNamingBug())

abstract class ExtendedTestRunner(cases: Map<String, String>) : StringSpec({
    withData(cases.workaroundKotestNamingBug()) {
        val case = json.decodeFromString<TestCase>(it)
        val clock = FixedClock(case.context.validationClock)
        val decisionService = DecisionService(clock)
        if (case.context.certificate == null) throw IllegalArgumentException("certificate")
        val certificateRepository = PrefilledCertificateRepository(case.context.certificate)
        val decodingChain = DefaultChain.buildVerificationChain(certificateRepository)
        val qrCodeContent = case.base45WithPrefix ?: if (case.qrCodePng != null) {
            try {
                // TODO decode QRCode?
                //DefaultTwoDimCodeService(350).decode(case.qrCodePng.fromBase64())
                case.qrCodePng
            } catch (e: Exception) {
                case.expectedResult.qrDecode?.let { if (it) throw e }; throw e
            }
        } else throw IllegalArgumentException("Input")

        val chainResult = decodingChain.decodeExtended(qrCodeContent)
        val verificationResult = chainResult.verificationResult
        val decision = decisionService.decide(verificationResult)

        case.expectedResult.qrDecode?.let {
            // TODO QRCode in Common and JS?
            //if (it) (case.base45WithPrefix, qrCodeContent)
            //if (!it) (VerificationDecision.FAIL, decision)
        }
        case.expectedResult.prefix?.let {
            withClue("Prefix") {
                if (it) chainResult.chainDecodeResult.step4Encoded shouldBe case.base45
                if (!it) decision shouldBe VerificationDecision.FAIL
            }
        }
        case.expectedResult.base45Decode?.let {
            withClue("Base45 Decoding") {
                if (it && case.compressedHex != null) {
                    chainResult.chainDecodeResult.step3Compressed?.toHexString()
                        ?.lowercase() shouldBe case.compressedHex.lowercase()
                }
                if (!it) verificationResult.error shouldBe VerificationResult.Error.BASE_45_DECODING_FAILED
            }
        }
        case.expectedResult.compression?.let {
            withClue("ZLib Decompression") {
                verificationResult.zlibDecoded shouldBe it
                if (it) {
                    chainResult.chainDecodeResult.step2Cose?.toHexString()
                        ?.lowercase() shouldBe case.coseHex?.lowercase()
                }

            }
        }
        case.expectedResult.coseSignature?.let {
            withClue("COSE Verify") {
                verificationResult.coseVerified shouldBe it
                if (!it) decision shouldBe VerificationDecision.FAIL
            }
        }
        case.expectedResult.cborDecode?.let {
            withClue("CBOR Decoding") {
                // Our implementation exits early with a COSE error
                if (case.expectedResult.coseSignature == false) {
                    verificationResult.cborDecoded shouldBe false
                    if (case.cborHex != null) {
                        val newResult = VerificationResult()
                        DefaultCborService().decode(case.cborHex.fromHexString(), newResult)
                        newResult.cborDecoded shouldBe it
                    }
                } else {
                    verificationResult.cborDecoded shouldBe it
                }
                if (it && case.expectedResult.coseSignature != false) {
                    chainResult.chainDecodeResult.eudgc shouldBe case.eudgc
                    // doesn't make sense to compare exact CBOR hex encoding
                    //assertThat(chainResult.step1Cbor.toHexString(), equalToIgnoringCase(case.cborHex))
                }
                if (!it) decision shouldBe VerificationDecision.FAIL
            }
        }
        case.expectedResult.json?.let {
            withClue("Green Pass fully decoded") {
                // Our implementation exits early with a COSE error
                if (case.expectedResult.coseSignature == false) {
                    chainResult.chainDecodeResult.eudgc shouldBe null
                    if (case.cborHex != null) {
                        val dgc = DefaultCborService().decode(case.cborHex.fromHexString(), VerificationResult())
                        dgc shouldBe case.eudgc
                    }
                } else {
                    chainResult.chainDecodeResult.eudgc shouldBe case.eudgc
                }
                if (!it) decision shouldBe VerificationDecision.FAIL
            }
        }
        case.expectedResult.schemaValidation?.let {
            withClue("Schema verification") {
                // Our implementation exits early with a COSE error
                if (case.expectedResult.coseSignature == false) {
                    if (case.cborHex != null) {
                        val newResult = VerificationResult()
                        DefaultSchemaValidationService().validate(case.cborHex.fromHexString(), newResult)
                        newResult.schemaValidated shouldBe it
                    }
                } else {
                    verificationResult.schemaValidated shouldBe it
                }
                if (!it) decision shouldBe VerificationDecision.FAIL
            }
        }
        case.expectedResult.expirationCheck?.let {
            withClue("Expiration Check") {
                if (case.expectedResult.coseSignature != false) {
                    if (it) decision shouldBe VerificationDecision.GOOD
                    if (!it) decision shouldBe VerificationDecision.FAIL
                }
            }
        }
        case.expectedResult.keyUsage?.let {
            withClue("Key Usage") {
                if (case.expectedResult.coseSignature != false) {
                    if (it) decision shouldBe VerificationDecision.GOOD
                    if (!it) decision shouldBe VerificationDecision.FAIL
                }
            }
        }
    }
})


