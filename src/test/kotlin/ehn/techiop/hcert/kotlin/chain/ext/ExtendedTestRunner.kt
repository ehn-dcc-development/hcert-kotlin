package ehn.techiop.hcert.kotlin.chain.ext

import ehn.techiop.hcert.kotlin.chain.Chain
import ehn.techiop.hcert.kotlin.chain.DecisionService
import ehn.techiop.hcert.kotlin.chain.VerificationDecision
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.fromBase64
import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultTwoDimCodeService
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.toHexString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalToIgnoringCase
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.time.Clock
import java.time.ZoneId

class ExtendedTestRunner {

    @ParameterizedTest
    @MethodSource("verificationProvider")
    fun verification(filename: String, case: TestCase) {
        println("Executing verification test case \"${filename}\": \"${case.context.description}\"")
        val clock = case.context.validationClock?.let {
            Clock.fixed(it.toInstant(), ZoneId.systemDefault())
        } ?: Clock.systemDefaultZone()
        val decisionService = DecisionService(clock)
        if (case.context.certificate == null) throw IllegalArgumentException("certificate")
        val certificateRepository = PrefilledCertificateRepository(case.context.certificate)
        val verificationResult = VerificationResult()
        val decodingChain = Chain.buildVerificationChain(certificateRepository)
        val qrCodeContent = if (case.qrCodePng != null) {
            try {
                DefaultTwoDimCodeService(350).decode(case.qrCodePng.fromBase64())
            } catch (e: Exception) {
                case.expectedResult.qrDecode?.let {
                    if (it) throw e
                }
                case.qrCodePng
            }
        } else {
            case.base45WithPrefix
        } ?: throw IllegalArgumentException("Input")

        val chainResult = decodingChain.decodeExtended(qrCodeContent, verificationResult)
        val decision = decisionService.decide(verificationResult)

        case.expectedResult.qrDecode?.let {
            if (it) assertThat(qrCodeContent, equalTo(case.base45WithPrefix))
            if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
        }
        case.expectedResult.prefix?.let {
            if (it) assertThat(chainResult.step4Encoded, equalTo(case.base45))
            if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
        }
        case.expectedResult.base45Decode?.let {
            assertThat(verificationResult.base45Decoded, equalTo(it))
            if (it && case.compressedHex != null) {
                assertThat(chainResult.step3Compressed.toHexString(), equalToIgnoringCase(case.compressedHex))
            }
            if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
        }
        case.expectedResult.compression?.let {
            assertThat(verificationResult.zlibDecoded, equalTo(it))
            if (it) assertThat(chainResult.step2Cose.toHexString(), equalToIgnoringCase(case.coseHex))
        }
        case.expectedResult.coseSignature?.let {
            assertThat(verificationResult.coseVerified, equalTo(it))
            if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
        }
        case.expectedResult.cborDecode?.let {
            assertThat(verificationResult.cborDecoded, equalTo(it))
            if (it) {
                assertThat(chainResult.eudgc, equalTo(case.eudgc?.toEuSchema()))
                // doesn't make sense to compare exact CBOR hex encoding
                //assertThat(chainResult.step1Cbor.toHexString(), equalToIgnoringCase(case.cborHex))
            }
            if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
        }
        case.expectedResult.json?.let {
            assertThat(chainResult.eudgc, equalTo(case.eudgc?.toEuSchema()))
            if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
        }
        case.expectedResult.schemaValidation?.let {
            // TODO Implement schema validation
            //assertThat(verificationResult.cborDecoded, equalTo(it))
            //if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
        }
        case.expectedResult.expirationCheck?.let {
            if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
        }
    }

    @ParameterizedTest
    @MethodSource("generationProvider")
    fun generation(filename: String, case: TestCase) {
        println("Executing generation test case \"${filename}\": \"${case.context.description}\"")
        if (case.eudgc == null) throw IllegalArgumentException("eudgc")
        val clock = case.context.validationClock?.let {
            Clock.fixed(it.toInstant(), ZoneId.systemDefault())
        } ?: Clock.systemDefaultZone()
        val creationChain = Chain(
            DefaultCborService(clock = clock),
            DefaultCoseService(RandomEcKeyCryptoService(clock = clock)),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            DefaultBase45Service()
        )

        val chainResult = creationChain.encode(case.eudgc.toEuSchema())

        case.expectedResult.schemaGeneration?.let {
            // TODO Implement schema verification
        }
        case.expectedResult.encodeGeneration?.let {
            assertThat(chainResult.step1Cbor.toHexString(), equalToIgnoringCase(case.cborHex))
        }
    }

    companion object {

        @JvmStatic
        @Suppress("unused")
        fun verificationProvider(): List<Arguments> {
            val testcaseFiles = listOf(
                "src/test/resources/AT01.json",
                "src/test/resources/AT02.json",
                "src/test/resources/AT03.json",
                "src/test/resources/AT04.json",
                "src/test/resources/HR01.json",
                "src/test/resources/HR02.json",
                "src/test/resources/HR03.json",
                "src/test/resources/SE01.json",
                "src/test/resources/SE02.json",
                "src/test/resources/SE03.json",
                "src/test/resources/SE04.json",
                // TODO CBOR Tag? "src/test/resources/SE05.json",
                "src/test/resources/SE06.json",
                "src/test/resources/BG01.json",
                "src/test/resources/RO01.json",
                "src/test/resources/RO02.json",
                "src/test/resources/testcaseQ1.json",
                //"src/test/resources/testcaseQ2.json",
                "src/test/resources/testcaseH1.json",
                "src/test/resources/testcaseH2.json",
                "src/test/resources/testcaseH3.json",
                "src/test/resources/testcaseB1.json",
                "src/test/resources/testcaseZ1.json",
                "src/test/resources/testcaseZ2.json",
                "src/test/resources/testcaseCO1.json",
                "src/test/resources/testcaseCO2.json",
                "src/test/resources/testcaseCO3.json",
                //"src/test/resources/testcaseCO4.json",
                "src/test/resources/testcaseCO5.json",
                "src/test/resources/testcaseCO6.json",
                "src/test/resources/testcaseCO7.json",
                "src/test/resources/testcaseCO8.json",
                "src/test/resources/testcaseCO9.json",
                "src/test/resources/testcaseCO10.json",
                "src/test/resources/testcaseCO11.json",
                "src/test/resources/testcaseCO12.json",
                "src/test/resources/testcaseCO13.json",
                "src/test/resources/testcaseCO14.json",
                "src/test/resources/testcaseCO15.json",
                "src/test/resources/testcaseCO16.json",
                "src/test/resources/testcaseCO17.json",
                "src/test/resources/testcaseCO18.json",
                "src/test/resources/testcaseCO19.json",
                "src/test/resources/testcaseCO20.json",
                "src/test/resources/testcaseCO21.json",
                "src/test/resources/testcaseCO22.json",
                "src/test/resources/testcaseCO23.json",
                "src/test/resources/testcaseCBO1.json",
                "src/test/resources/testcaseCBO2.json",
                "src/test/resources/testcaseDGC1.json",
                "src/test/resources/testcaseDGC2.json",
                "src/test/resources/testcaseDGC3.json",
                "src/test/resources/testcaseDGC4.json",
                "src/test/resources/testcaseDGC5.json",
                "src/test/resources/testcaseDGC6.json",
            )
            return testcaseFiles.map { Arguments.of(it, Json.decodeFromString<TestCase>(File(it).bufferedReader().readText())) }
        }

        @JvmStatic
        @Suppress("unused")
        fun generationProvider(): List<Arguments> {
            val testcaseFiles = listOf(
                "src/test/resources/gentestcase01.json",
                "src/test/resources/gentestcase02.json",
                "src/test/resources/gentestcase03.json",
                "src/test/resources/gentestcase04.json",
            )
            return testcaseFiles.map { Arguments.of(it, Json.decodeFromString<TestCase>(File(it).bufferedReader().readText())) }
        }

    }

}
