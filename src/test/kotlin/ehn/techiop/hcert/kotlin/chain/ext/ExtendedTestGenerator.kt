package ehn.techiop.hcert.kotlin.chain.ext

import com.fasterxml.jackson.databind.ObjectMapper
import ehn.techiop.hcert.data.Eudgc
import ehn.techiop.hcert.kotlin.chain.Chain
import ehn.techiop.hcert.kotlin.chain.ChainResult
import ehn.techiop.hcert.kotlin.chain.SampleData
import ehn.techiop.hcert.kotlin.chain.asBase64
import ehn.techiop.hcert.kotlin.chain.faults.FaultyBase45Service
import ehn.techiop.hcert.kotlin.chain.faults.FaultyCompressorService
import ehn.techiop.hcert.kotlin.chain.faults.NoopCompressorService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultTwoDimCodeService
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.toHexString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.security.cert.X509Certificate
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

@Disabled("Don't want to generate test case files every time")
class ExtendedTestGenerator {

    @Test
    fun writeGen01Vaccination() {
        val eudgc = ObjectMapper().readValue(SampleData.vaccination, Eudgc::class.java)
        val clock = Clock.fixed(Instant.parse("2021-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clock)
        val chain = Chain(
            // TODO Would also need to specify validity, country code!
            DefaultCborService(clock = clock),
            DefaultCoseService(cryptoService),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            DefaultBase45Service()
        )
        val result = chain.encode(eudgc)

        createGenerationTestCaseJson(
            clock, eudgc, result, "Success, vaccination", "gentestcase01",
            TestExpectedResults(
                verifySchemaGeneration = true,
                verifyEncodeGeneration = true,
            )
        )
    }

    @Test
    fun writeGen02Recovery() {
        val eudgc = ObjectMapper().readValue(SampleData.recovery, Eudgc::class.java)
        val clock = Clock.fixed(Instant.parse("2021-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clock)
        val chain = Chain(
            DefaultCborService(clock = clock),
            DefaultCoseService(cryptoService),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            DefaultBase45Service()
        )
        val result = chain.encode(eudgc)

        createGenerationTestCaseJson(
            clock, eudgc, result, "Success, recovery", "gentestcase02",
            TestExpectedResults(
                verifySchemaGeneration = true,
                verifyEncodeGeneration = true,
            )
        )
    }

    @Test
    fun writeGen03TestNaa() {
        val eudgc = ObjectMapper().readValue(SampleData.testNaa, Eudgc::class.java)
        val clock = Clock.fixed(Instant.parse("2021-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clock)
        val chain = Chain(
            DefaultCborService(clock = clock),
            DefaultCoseService(cryptoService),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            DefaultBase45Service()
        )
        val result = chain.encode(eudgc)

        createGenerationTestCaseJson(
            clock, eudgc, result, "Success, test NAA", "gentestcase03",
            TestExpectedResults(
                verifySchemaGeneration = true,
                verifyEncodeGeneration = true,
            )
        )
    }

    @Test
    fun writeGen04TestRat() {
        val eudgc = ObjectMapper().readValue(SampleData.testRat, Eudgc::class.java)
        val clock = Clock.fixed(Instant.parse("2021-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clock)
        val chain = Chain(
            DefaultCborService(clock = clock),
            DefaultCoseService(cryptoService),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            DefaultBase45Service()
        )
        val result = chain.encode(eudgc)

        createGenerationTestCaseJson(
            clock, eudgc, result, "Success, test RAT", "gentestcase04",
            TestExpectedResults(
                verifySchemaGeneration = true,
                verifyEncodeGeneration = true,
            )
        )
    }

    @Test
    fun write01Good() {
        val eudgc = ObjectMapper().readValue(SampleData.vaccination, Eudgc::class.java)
        val clock = Clock.fixed(Instant.parse("2021-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clock)
        val chain = Chain(
            DefaultCborService(clock = clock),
            DefaultCoseService(cryptoService),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            DefaultBase45Service()
        )
        val result = chain.encode(eudgc)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgc, result, cryptoService.getCertificate(), qrCode,
            "All good", "testcase01",
            TestExpectedResults(
                verifyQrDecode = true,
                verifyPrefix = true,
                verifyBase45Decode = true,
                verifyCompression = true,
                verifyCoseSignature = true,
                verifyCborDecode = true,
                verifyJson = true,
                verifySchemaValidation = true,
                expired = false
            )
        )
    }

    @Test
    fun write02SignatureFailed() {
        val eudgc = ObjectMapper().readValue(SampleData.vaccination, Eudgc::class.java)
        val clock = Clock.fixed(Instant.parse("2021-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clock)
        val chain = Chain(
            DefaultCborService(clock = clock),
            DefaultCoseService(RandomEcKeyCryptoService(clock = clock)),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            DefaultBase45Service()
        )
        val result = chain.encode(eudgc)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgc, result, cryptoService.getCertificate(), qrCode,
            "Signature cert not in trust list", "testcase02",
            TestExpectedResults(
                verifyQrDecode = true,
                verifyPrefix = true,
                verifyBase45Decode = true,
                verifyCompression = true,
                verifyCoseSignature = false,
                verifyCborDecode = true,
                verifyJson = true,
                verifySchemaValidation = true,
                expired = false
            )
        )
    }

    @Test
    fun write03Expired() {
        val eudgc = ObjectMapper().readValue(SampleData.vaccination, Eudgc::class.java)
        val clock = Clock.fixed(Instant.parse("2018-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clock)
        val chain = Chain(
            DefaultCborService(clock = clock),
            DefaultCoseService(cryptoService),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            DefaultBase45Service()
        )
        val result = chain.encode(eudgc)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            Clock.fixed(Instant.parse("2021-05-03T18:00:00Z"), ZoneId.systemDefault()),
            eudgc, result, cryptoService.getCertificate(), qrCode,
            "Certificate expired", "testcase03",
            TestExpectedResults(
                verifyQrDecode = true,
                verifyPrefix = true,
                verifyBase45Decode = true,
                verifyCompression = true,
                verifyCoseSignature = true,
                verifyCborDecode = true,
                verifyJson = true,
                verifySchemaValidation = true,
                expired = true
            )
        )
    }

    @Test
    fun writeQ1QrCodeBroken() {
        val eudgc = ObjectMapper().readValue(SampleData.vaccination, Eudgc::class.java)
        val clock = Clock.fixed(Instant.parse("2021-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clock)
        val chain = Chain(
            DefaultCborService(clock = clock),
            DefaultCoseService(cryptoService),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            DefaultBase45Service()
        )
        val result = chain.encode(eudgc)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).also { it.shuffle() }.asBase64()

        createVerificationTestCaseJson(
            clock, eudgc, result, cryptoService.getCertificate(), qrCode,
            "QR code cannot be read", "testcaseQ1",
            TestExpectedResults(
                verifyQrDecode = false,
            )
        )
    }

    @Test
    fun writeQ2QrCodeWarning() {
        // TODO How to generate a QR Code with a wrong encoding? Our library picks the best mode available
    }

    @Test
    fun writeQ3QrCodeWarning() {
        // TODO How to generate a QR Code over the maximum size? What content to put in there?
    }

    @Test
    fun writeH1ContextInvalid() {
        val eudgc = ObjectMapper().readValue(SampleData.vaccination, Eudgc::class.java)
        val clock = Clock.fixed(Instant.parse("2021-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clock)
        val chain = Chain(
            DefaultCborService(clock = clock),
            DefaultCoseService(cryptoService),
            DefaultContextIdentifierService("HL0:"),
            DefaultCompressorService(),
            DefaultBase45Service()
        )
        val result = chain.encode(eudgc)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgc, result, cryptoService.getCertificate(), qrCode,
            "Context identifier does not match schema (HL0:)", "testcaseH1",
            TestExpectedResults(
                verifyQrDecode = true,
                verifyPrefix = false,
            )
        )
    }

    @Test
    fun writeH2ContextInvalid() {
        val eudgc = ObjectMapper().readValue(SampleData.vaccination, Eudgc::class.java)
        val clock = Clock.fixed(Instant.parse("2021-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clock)
        val chain = Chain(
            DefaultCborService(clock = clock),
            DefaultCoseService(cryptoService),
            DefaultContextIdentifierService("HC2:"),
            DefaultCompressorService(),
            DefaultBase45Service()
        )
        val result = chain.encode(eudgc)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgc, result, cryptoService.getCertificate(), qrCode,
            "Context identifier not supported (HC2:)", "testcaseH2",
            TestExpectedResults(
                verifyQrDecode = true,
                verifyPrefix = false,
            )
        )
    }

    @Test
    fun writeB1Base45Invalid() {
        val eudgc = ObjectMapper().readValue(SampleData.vaccination, Eudgc::class.java)
        val clock = Clock.fixed(Instant.parse("2021-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clock)
        val chain = Chain(
            DefaultCborService(clock = clock),
            DefaultCoseService(cryptoService),
            DefaultContextIdentifierService(),
            DefaultCompressorService(),
            FaultyBase45Service()
        )
        val result = chain.encode(eudgc)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgc, result, cryptoService.getCertificate(), qrCode,
            "Bae45 encoding broken", "testcaseB1",
            TestExpectedResults(
                verifyQrDecode = true,
                verifyPrefix = true,
                verifyBase45Decode = false
            )
        )
    }

    @Test
    fun writeZ1CompressionInvalid() {
        val eudgc = ObjectMapper().readValue(SampleData.vaccination, Eudgc::class.java)
        val clock = Clock.fixed(Instant.parse("2021-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clock)
        val chain = Chain(
            DefaultCborService(clock = clock),
            DefaultCoseService(cryptoService),
            DefaultContextIdentifierService(),
            FaultyCompressorService(),
            DefaultBase45Service()
        )
        val result = chain.encode(eudgc)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgc, result, cryptoService.getCertificate(), qrCode,
            "Compression broken", "testcaseZ1",
            TestExpectedResults(
                verifyQrDecode = true,
                verifyPrefix = true,
                verifyBase45Decode = true,
                verifyCompression = false
            )
        )
    }

    @Test
    fun writeZ2CompressionNone() {
        val eudgc = ObjectMapper().readValue(SampleData.vaccination, Eudgc::class.java)
        val clock = Clock.fixed(Instant.parse("2021-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clock)
        val chain = Chain(
            DefaultCborService(clock = clock),
            DefaultCoseService(cryptoService),
            DefaultContextIdentifierService(),
            NoopCompressorService(),
            DefaultBase45Service()
        )
        val result = chain.encode(eudgc)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgc, result, cryptoService.getCertificate(), qrCode,
            "Compression omitted", "testcaseZ2",
            TestExpectedResults(
                verifyQrDecode = true,
                verifyPrefix = true,
                verifyBase45Decode = true,
                verifyCompression = false,
                verifyCoseSignature = true
            )
        )
    }

    private fun createVerificationTestCaseJson(
        clock: Clock,
        eudgc: Eudgc,
        result: ChainResult,
        certificate: X509Certificate,
        qrCode: String,
        description: String,
        filename: String,
        expectedResult: TestExpectedResults
    ) {
        val context = TestContext(
            version = 1,
            schema = "1.0.0",
            certificate = certificate,
            validationClock = OffsetDateTime.ofInstant(clock.instant(), clock.zone),
            description = description
        )
        val testcase = TestCase(
            eudgc = eudgc,
            cborHex = result.step1Cbor.toHexString(),
            coseHex = result.step2Cose.toHexString(),
            compressedHex = result.step3Compressed.toHexString(),
            base45 = result.step4Encoded,
            base45WithPrefix = result.step5Prefixed,
            qrCodePng = qrCode,
            context = context,
            expectedResult = expectedResult
        )
        File("src/test/resources/$filename.json").bufferedWriter().use {
            it.write(Json { prettyPrint = true }.encodeToString(testcase))
        }
    }

    private fun createGenerationTestCaseJson(
        clock: Clock,
        eudgc: Eudgc,
        result: ChainResult,
        description: String,
        filename: String,
        expectedResult: TestExpectedResults
    ) {
        val context = TestContext(
            version = 1,
            schema = "1.0.0",
            certificate = null,
            validationClock = OffsetDateTime.ofInstant(clock.instant(), clock.zone),
            description = description
        )
        val testcase = TestCase(
            eudgc = eudgc,
            cborHex = result.step1Cbor.toHexString(),
            coseHex = null,
            compressedHex = null,
            base45 = null,
            base45WithPrefix = null,
            qrCodePng = null,
            context = context,
            expectedResult = expectedResult
        )
        File("src/test/resources/$filename.json").bufferedWriter().use {
            it.write(Json { prettyPrint = true }.encodeToString(testcase))
        }
    }


}
