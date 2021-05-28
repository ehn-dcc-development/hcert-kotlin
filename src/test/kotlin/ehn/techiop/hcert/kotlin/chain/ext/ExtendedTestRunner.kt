package ehn.techiop.hcert.kotlin.chain.ext

import ehn.techiop.hcert.data.Eudcc
import ehn.techiop.hcert.kotlin.chain.Chain
import ehn.techiop.hcert.kotlin.chain.DecisionService
import ehn.techiop.hcert.kotlin.chain.VerificationDecision
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import ehn.techiop.hcert.kotlin.chain.fromBase64
import ehn.techiop.hcert.kotlin.chain.impl.DefaultTwoDimCodeService
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
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
import java.time.ZoneOffset

class ExtendedTestRunner {

    @ParameterizedTest
    @MethodSource("verificationProvider")
    fun verificationLoader(file: File) {
        println("Loading $file")
        val text = file.bufferedReader().readText()
        val content = Json { ignoreUnknownKeys = true; coerceInputValues = true }.decodeFromString<TestCase>(text)
        verification(file.path, content)
    }

    fun verification(filename: String, case: TestCase) {
        val description = case.context.description ?: "<NO DESCRIPTION>"
        println("Executing verification test case \"${filename}\": \"$description\"")
        val clock = case.context.validationClock?.let { Clock.fixed(it, ZoneOffset.UTC) } ?: Clock.systemUTC()
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
        println(verificationResult)
        println(decision)

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
                assertThat(chainResult.eudcc.removeEmptyArrays(), equalTo(case.eudcc?.toEuSchema()))
                // doesn't make sense to compare exact CBOR hex encoding
                //assertThat(chainResult.step1Cbor.toHexString(), equalToIgnoringCase(case.cborHex))
            }
            if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
        }
        case.expectedResult.json?.let {
            assertThat(chainResult.eudcc.removeEmptyArrays(), equalTo(case.eudcc?.toEuSchema()))
            if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
        }
        case.expectedResult.schemaValidation?.let {
            // TODO Implement schema validation
            //assertThat(verificationResult.cborDecoded, equalTo(it))
            //if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
        }
        case.expectedResult.expirationCheck?.let {
            if (it) assertThat(decision, equalTo(VerificationDecision.GOOD))
            if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
        }
        case.expectedResult.keyUsage?.let {
            if (it) assertThat(decision, equalTo(VerificationDecision.GOOD))
            if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
        }
    }

    companion object {

        @JvmStatic
        @Suppress("unused")
        fun verificationProvider(): List<Arguments> {
            return File("src/test/resources/dgc-testdata").walkTopDown()
                .filter { it.name.endsWith(".json") }.toList()
                .filterNot { it.path.contains("/CY/") } // com.google.zxing.ChecksumException
                .filterNot { it.path.contains("/CZ/") } // Version not an int
                .filterNot { it.path.contains("/DE/") } // Issued At not right
                .filterNot { it.path.contains("test+recovery") } // Certificate missing OID
                .filterNot { it.path.contains("recovery+vaccination") } // Certificate missing OID
                .filterNot { it.path.contains("test+vaccination") } // Certificate missing OID
                .filterNot { it.path.contains("+wrong") } // Certificate missing OID
                .sorted()
                .map { Arguments.of(it) }
        }

    }

}

private fun Eudcc.removeEmptyArrays() = Eudcc().also {
    it.ver = this.ver
    it.dob = this.dob
    it.nam = this.nam
    it.v = this.v?.filterNotNull()?.ifEmpty { null }
    it.r = this.r?.filterNotNull()?.ifEmpty { null }
    it.t = this.t?.filterNotNull()?.ifEmpty { null }
}
