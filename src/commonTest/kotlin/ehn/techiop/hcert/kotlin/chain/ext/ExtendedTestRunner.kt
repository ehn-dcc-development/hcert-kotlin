package ehn.techiop.hcert.kotlin.chain.ext

import ehn.techiop.hcert.kotlin.chain.*
import ehn.techiop.hcert.kotlin.chain.impl.DefaultHigherOrderValidationService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultSchemaValidationService
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.impl.VerificationCoseService
import ehn.techiop.hcert.kotlin.trust.CwtHelper
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

expect fun allOfficialTestCases(): Map<String, String>
private val json = Json { ignoreUnknownKeys = true }
private fun Map<String, String>.workaroundKotestNamingBug() =
    this.map { (k, v) -> Pair(k.replace(".", "\u2024"), v) }.toMap()


class CommonTests : ExtendedTestRunner(allOfficialTestCases()
    .filter { it.key.contains("common/") }
    .filterNot { it.key.contains("DGC2") } // Testcase is not correct?
    .filterNot { it.key.contains("CBO1") } // Test case not correct? other testcases not correct? Expected :CBOR_DESERIALIZATION_FAILED,  Actual   :SCHEMA_VALIDATION_FAILED, but at least in JS, this is tough to distinguish
    .workaroundKotestNamingBug())


class MemberstateTests : ExtendedTestRunner(allOfficialTestCases()
    .filterNot { it.key.contains("common/") }
    // Errors in context files:
    .filterNot { it.key.contains("CZ/") } // Error in test context files: Version not an int
    // Logical errors in test files:
    //.filterNot { it.key.contains("BE/2DCode/raw/3") } // MissingFieldException: Fields [tg, tt, tr, tc, co, is, ci] are required for type with serial name 'ehn.techiop.hcert.kotlin.data.Test', but they were missing
    .filterNot { it.key.contains("BG/2DCode/raw/1") } // DateTimeParseException: Text '2021-03-09T00:00:00' could not be parsed, unparsed text found at index 10
    .filterNot { it.key.contains("BG/2DCode/raw/2") } // DateTimeParseException: Text '2021-05-11T00:00:00' could not be parsed, unparsed text found at index 10
    .filterNot { it.key.contains("BG/2DCode/raw/4") } // Throwable: issuedAt<certValidFrom
    .filterNot { it.key.contains("ES/2DCode/raw/1101") } // Throwable: issuedAt>clock.now()
    .filterNot { it.key.contains("ES/2DCode/raw/1102") } // Throwable: issuedAt>clock.now()
    .filterNot { it.key.contains("ES/2DCode/raw/1103") } // Throwable: issuedAt>clock.now()
    .filterNot { it.key.contains("ES/2DCode/raw/2101") } // Throwable: issuedAt>clock.now()
    .filterNot { it.key.contains("ES/2DCode/raw/2102") } // Throwable: issuedAt>clock.now()
    .filterNot { it.key.contains("ES/2DCode/raw/2103") } // Throwable: issuedAt>clock.now()
    .filterNot { it.key.contains("HU/2DCode/raw/") } // Throwable: expirationTime>certValidUntil
    .filterNot { it.key.contains("IE/2DCode/Raw/1") } // Declares illegal schema version 1.0.4 (but would otherwise verify fine against 1.2/1.3)
    .filterNot { it.key.contains("IE/2DCode/Raw/2") } // Declares illegal schema version 1.0.4 (but would otherwise verify fine against 1.2/1.3)
    .filterNot { it.key.contains("IE/2DCode/Raw/3") } // Declares illegal schema version 1.0.4 (but would otherwise verify fine against 1.2/1.3)
    .filterNot { it.key.contains("IE/2DCode/Raw/4") } // Declares illegal schema version 1.0.4 (but would otherwise verify fine against 1.2/1.3)
    .filterNot { it.key.contains("FR/2DCode/raw/recovery") } // Throwable: issuedAt>clock.now()
    .filterNot { it.key.contains("FR/2DCode/raw/test_pcr") } // Throwable: issuedAt>clock.now()
    .filterNot { it.key.contains("FR/2DCode/raw/vaccin") } // Throwable: issuedAt>clock.now()
    .filterNot { it.key.contains("LI/2DCode/raw/4.json") } // Throwable: Field 'ver' is missing
    .filterNot { it.key.contains("LT/2DCode/raw/1.json") } // DateTimeParseException: Text '2021-06-10T12:02:09.003+0000:00' could not be parsed, unparsed text found at index 26
    .filterNot { it.key.contains("LT/2DCode/raw/2.json") } // DateTimeParseException: Text '2021-06-10T12:02:09.003+0000:00' could not be parsed, unparsed text found at index 26
    .filterNot { it.key.contains("LT/2DCode/raw/3.json") } // DateTimeParseException: Text '2021-06-10T12:02:09.003+0000:00' could not be parsed, unparsed text found at index 26
    .filterNot { it.key.contains("LU/2DCode/raw/INCERT_R_DCC_Recovery") } // Throwable: issuedAt>clock.now()
    .filterNot { it.key.contains("PL/1.0.0/2DCode/raw/10") } // Throwable: issuedAt<certValidFrom
    .filterNot { it.key.contains("PL/1.2.1/2DCode/raw/10") } // Throwable: issuedAt<certValidFrom
    .filterNot { it.key.contains("PL/1.0.0/2DCode/raw/11") } // DateTimeParseException: Text '2021-03-18T22:54:00+02:00' could not be parsed, unparsed text found at index 10
    .filterNot { it.key.contains("PL/1.0.0/2DCode/raw/12") } // DateTimeParseException: Text '2021-03-18T22:54:00+02:00' could not be parsed, unparsed text found at index 10
    .filterNot { it.key.contains("PL/1.0.0/2DCode/raw/13") } // DateTimeParseException: Text '2021-03-18T22:54:00+02:00' could not be parsed, unparsed text found at index 10
    .filterNot { it.key.contains("PL/1.2.1/2DCode/raw/11") } // DateTimeParseException: Text '2021-03-18T22:54:00+02:00' could not be parsed, unparsed text found at index 10
    .filterNot { it.key.contains("PL/1.2.1/2DCode/raw/12") } // DateTimeParseException: Text '2021-03-18T22:54:00+02:00' could not be parsed, unparsed text found at index 10
    .filterNot { it.key.contains("PL/1.2.1/2DCode/raw/13") } // DateTimeParseException: Text '2021-03-18T22:54:00+02:00' could not be parsed, unparsed text found at index 10
    .filterNot { it.key.contains("NL/2DCode/raw/005") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/006") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/011") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/014") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/018") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/020") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/022") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/030") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/038") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/045") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/046") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/054") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/057") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/059") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/060") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/062") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/070") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/071") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/073") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/078") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/079") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/086") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/094") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/095") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/102") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/104") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/109") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/110") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/118") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/120") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/121") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/123") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/126") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/130") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/134") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/139") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/141") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/142") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/143") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/144") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/150") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/153") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/158") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/162") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/163") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/165") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/166") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/169") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/174") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/178") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/182") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/186") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/189") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/190") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/192") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/198") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/201") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/206") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/214") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/215") } // Data does not follow schema
    .filterNot { it.key.contains("NL/2DCode/raw/007") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/015") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/023") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/031") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/039") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/047") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/055") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/063") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/064") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/065") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/066") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/067") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/068") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/069") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/087") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/103") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/111") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/119") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/127") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/135") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/136") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/137") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/138") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/140") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/151") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/159") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/167") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/175") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/183") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/191") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/199") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/207") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/208") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/209") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/210") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/211") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/212") } // Validates, but Testcase claims schema error
    .filterNot { it.key.contains("NL/2DCode/raw/213") } // Validates, but Testcase claims schema error

    .filterNot { it.key.contains("NL-test+wrong") } // borked
    //.filterNot { it.key.contains("test+recovery") } // Certificate missing OID
    //  .filterNot { it.key.contains("recovery+vaccination") } // Certificate missing OID
    //.filterNot { it.key.contains("test+vaccination") } // Certificate missing OID
    .filterNot { it.key.contains("IS/2DCode/raw/3") } // Wrong test on key usage: Expected UNSUITABLE_PUBLIC_KEY_TYPE but actual was null, see https://github.com/eu-digital-green-certificates/dgc-testdata/issues/244
    .filterNot { it.key.contains("ES/2DCode/raw/401") } // ECDSA Signature Length: https://github.com/eu-digital-green-certificates/dgc-testdata/issues/285
    .filterNot { it.key.contains("ES/2DCode/raw/402") } // ECDSA Signature Length: https://github.com/eu-digital-green-certificates/dgc-testdata/issues/285
    .filterNot { it.key.contains("ES/2DCode/raw/403") } // ECDSA Signature Length: https://github.com/eu-digital-green-certificates/dgc-testdata/issues/285

    // Probably not an error for us:
    .filterNot { it.key.contains("PL/1.0.0/2DCode/raw/7") } // Expected SCHEMA_VALIDATION_FAILED but actual was null: Entry for "ma"="9999" not in value set
    .filterNot { it.key.contains("PL/1.0.0/2DCode/raw/8") } // Expected SCHEMA_VALIDATION_FAILED but actual was null: Country not valid" "co"="XY"
    .filterNot { it.key.contains("PL/1.0.0/2DCode/raw/9") } // Expected SCHEMA_VALIDATION_FAILED but actual was null: Entry for "ma"="ORG-99999999" not in value set
    .filterNot { it.key.contains("PL/1.2.1/2DCode/raw/7") } // Expected SCHEMA_VALIDATION_FAILED but actual was null: Entry for "ma"="9999" not in value set
    .filterNot { it.key.contains("PL/1.2.1/2DCode/raw/8") } // Expected SCHEMA_VALIDATION_FAILED but actual was null: Country not valid" "co"="XY"
    .filterNot { it.key.contains("PL/1.2.1/2DCode/raw/9") } // Expected SCHEMA_VALIDATION_FAILED but actual was null: Entry for "ma"="ORG-99999999" not in value set
    .workaroundKotestNamingBug())

abstract class ExtendedTestRunner(cases: Map<String, String>) : StringSpec({
    withData(cases.workaroundKotestNamingBug()) {
        val case = json.decodeFromString<TestCase>(it)
        val clock = FixedClock(case.context.validationClock)
        if (case.context.certificate == null) throw IllegalArgumentException("certificate")
        val certificateRepository = PrefilledCertificateRepository(case.context.certificate)
        val decodingChain = DefaultChain.buildVerificationChain(certificateRepository, clock)
        val qrCodeContent = case.base45WithPrefix ?: if (case.qrCodePng != null) {
            try {
                // TODO decode QRCode?
                //DefaultTwoDimCodeService(350).decode(case.qrCodePng.fromBase64())
                case.qrCodePng
            } catch (e: Exception) {
                case.expectedResult.qrDecode?.let { if (it) throw e }; throw e
            }
        } else throw IllegalArgumentException("Input")

        val chainResult = decodingChain.decode(qrCodeContent)
        val verificationResult = chainResult.verificationResult
        var errorExpected = false

        case.expectedResult.qrDecode?.let {
            // TODO QRCode in Common and JS?
            //if (it) (case.base45WithPrefix, qrCodeContent)
            //if (!it) (VerificationDecision.FAIL, decision)
        }
        case.expectedResult.prefix?.let {
            withClue("Prefix") {
                if (it) {
                    chainResult.chainDecodeResult.step4Encoded shouldBe case.base45
                }
                if (!it) {
                    verificationResult.error shouldBe Error.INVALID_SCHEME_PREFIX
                    errorExpected = true
                }
            }
        }
        case.expectedResult.base45Decode?.let {
            withClue("Base45 Decoding") {
                if (it && case.compressedHex != null) {
                    chainResult.chainDecodeResult.step3Compressed?.toHexString()
                        ?.lowercase() shouldBe case.compressedHex.lowercase()
                }
                if (!it) {
                    verificationResult.error shouldBe Error.BASE_45_DECODING_FAILED
                    errorExpected = true
                }
            }
        }
        case.expectedResult.compression?.let {
            withClue("ZLib Decompression") {
                if (it) {
                    chainResult.chainDecodeResult.step2Cose?.toHexString()
                        ?.lowercase() shouldBe case.coseHex?.lowercase()
                }
                if (!it) {
                    verificationResult.error shouldBe Error.DECOMPRESSION_FAILED
                    errorExpected = true
                }
            }
        }
        case.expectedResult.coseSignature?.let {
            withClue("COSE Verify") {
                if (it) {
                    // serialization is different, makes no sense to compare in case of success
                    //chainResult.chainDecodeResult.step0Cbor?.toHexString()?.lowercase() shouldBe case.cborHex?.lowercase()
                    if (case.eudgc != null) {
                        chainResult.chainDecodeResult.eudgc shouldBe case.eudgc
                    } else if (case.coseHex != null) {
                        val newResult = VerificationResult()
                        VerificationCoseService(certificateRepository).decode(case.coseHex.fromHexString(), newResult)
                        newResult.error shouldBe null
                    }
                }
                if (!it) {
                    verificationResult.error shouldBeIn listOf(
                        Error.SIGNATURE_INVALID,
                        Error.KEY_NOT_IN_TRUST_LIST
                    )
                    errorExpected = true
                }
            }
        }
        case.expectedResult.cborDecode?.let {
            withClue("CBOR Decoding") {
                // Our implementation exits early with a COSE error
                if (errorExpected) {
                    if (case.cborHex != null) {
                        val newResult = VerificationResult()

                        //TODO cleanup test cases for new chain
                        DefaultHigherOrderValidationService().validate(
                            DefaultSchemaValidationService().validate(
                                CwtHelper.fromCbor(case.cborHex.fromHexString()).toCborObject(),
                                newResult
                            ), newResult
                        )
                        newResult.error shouldBe null
                    }
                } else {
                    if (it) {
                        chainResult.chainDecodeResult.eudgc shouldBe case.eudgc
                    }
                    if (!it) {
                        verificationResult.error shouldBe Error.CBOR_DESERIALIZATION_FAILED
                        errorExpected = true
                    }
                }
            }
        }
        case.expectedResult.json?.let {
            withClue("Green Pass fully decoded") {
                // Our implementation exits early with a COSE error
                if (errorExpected) {
                    chainResult.chainDecodeResult.eudgc shouldBe null
                    if (case.cborHex != null) {
                        //TODO cleanup test cases for new chain
                        val newResult = VerificationResult()
                        val dgc =
                            DefaultHigherOrderValidationService().validate(
                                DefaultSchemaValidationService().validate(
                                    CwtHelper.fromCbor(case.cborHex.fromHexString()).toCborObject(),
                                    newResult
                                ), newResult
                            )
                        dgc shouldBe case.eudgc
                    }
                } else {
                    chainResult.chainDecodeResult.eudgc shouldBe case.eudgc
                }
                if (!it) {
                    verificationResult.error shouldBe Error.CBOR_DESERIALIZATION_FAILED
                    errorExpected = true
                }
            }
        }
        case.expectedResult.schemaValidation?.let {
            withClue("Schema verification") {
                // Our implementation exits early with a COSE error
                if (errorExpected && case.cborHex != null) {
                    val newResult = VerificationResult()
                    DefaultSchemaValidationService().validate(
                        CwtHelper.fromCbor(case.cborHex.fromHexString()).toCborObject(), newResult
                    )
                    if (it) newResult.error shouldBe null
                    if (!it) newResult.error shouldBe Error.SCHEMA_VALIDATION_FAILED
                }
                if (!it) {
                    verificationResult.error shouldBe Error.SCHEMA_VALIDATION_FAILED
                    errorExpected = true
                }
            }
        }
        case.expectedResult.expirationCheck?.let {
            withClue("Expiration Check") {
                if (!errorExpected && !it) {
                    verificationResult.error shouldBe Error.CWT_EXPIRED
                    errorExpected = true
                }
            }
        }
        case.expectedResult.keyUsage?.let {
            withClue("Key Usage") {
                if (!errorExpected && !it) {
                    verificationResult.error shouldBe Error.UNSUITABLE_PUBLIC_KEY_TYPE
                    errorExpected = true
                }
            }
        }
        if (!errorExpected)
            verificationResult.error shouldBe null
    }
})


