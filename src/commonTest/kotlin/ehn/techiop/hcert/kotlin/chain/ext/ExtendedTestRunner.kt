package ehn.techiop.hcert.kotlin.chain.ext

import ehn.techiop.hcert.kotlin.chain.DecisionService
import ehn.techiop.hcert.kotlin.chain.DefaultChain
import ehn.techiop.hcert.kotlin.chain.VerificationDecision
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.toHexString
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

expect fun allOfficialTestCases(): Map<String, String>
private val json = Json { ignoreUnknownKeys = true }
private fun Map<String, String>.workaroundKotestNamingBug() =
    this.map { (k, v) -> Pair(k.replace(".json", "·json"), v) }.toMap()


class CommonTests : ExtendedTestRunner(allOfficialTestCases()
    .filter { it.key.contains("common/") }
    .filterNot { it.key.contains("DGC1.json") } //TODO @ckollmann Implement JVM Schema validation
    .filterNot { it.key.contains("DGC2.json") } //TODO @ckollmann Schema validation????
    .workaroundKotestNamingBug())

class MemberstateTests : ExtendedTestRunner(allOfficialTestCases().filterNot { it.key.contains("common/") }
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
    .workaroundKotestNamingBug())

abstract class ExtendedTestRunner(cases: Map<String, String>) : StringSpec({
    withData(cases.workaroundKotestNamingBug()) {
        val case = json.decodeFromString<TestCase>(it)
        val clock = case.context.validationClock?.let { FixedClock(it) } ?: Clock.System
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
                if (!it) decision shouldBe VerificationDecision.FAIL_QRCODE
            }
        }
        case.expectedResult.base45Decode?.let {
            withClue("Base45 Decoding") {
                verificationResult.base45Decoded shouldBe it
                if (it && case.compressedHex != null) {
                    chainResult.chainDecodeResult.step3Compressed?.toHexString()
                        ?.lowercase() shouldBe case.compressedHex.lowercase()
                }
                if (!it) decision shouldBe VerificationDecision.FAIL_QRCODE
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
                if (!it) decision shouldBe VerificationDecision.FAIL_SIGNATURE
            }
        }
        case.expectedResult.cborDecode?.let {
            withClue("CBOR Decoding") {
                verificationResult.cborDecoded shouldBe it
                if (it) {
                    chainResult.chainDecodeResult.eudgc shouldBe case.eudgc
                    // doesn't make sense to compare exact CBOR hex encoding
                    //assertThat(chainResult.step1Cbor.toHexString(), equalToIgnoringCase(case.cborHex))
                }
                if (!it) decision shouldBe VerificationDecision.FAIL_QRCODE
            }
        }
        case.expectedResult.json?.let {
            withClue("Green Pass fully decoded") {
                chainResult.chainDecodeResult.eudgc shouldBe case.eudgc
                if (!it) decision shouldBe VerificationDecision.FAIL_QRCODE
            }
        }
        case.expectedResult.schemaValidation?.let {
            withClue("Schema verification") {
                verificationResult.schemaValidated shouldBe it
                if (!it) decision shouldBe VerificationDecision.FAIL_QRCODE
            }
        }
        case.expectedResult.expirationCheck?.let {
            withClue("Expiration Check") {
                if (it) decision shouldBe VerificationDecision.GOOD
                if (!it) decision shouldBe VerificationDecision.FAIL_VALIDITY
            }
        }
        case.expectedResult.keyUsage?.let {
            withClue("Key Usage") {
                if (it) decision shouldBe VerificationDecision.GOOD
                if (!it) decision shouldBe VerificationDecision.FAIL_SIGNATURE
            }
        }
    }
})


