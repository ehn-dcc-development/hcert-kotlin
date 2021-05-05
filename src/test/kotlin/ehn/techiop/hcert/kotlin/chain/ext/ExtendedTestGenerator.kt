package ehn.techiop.hcert.kotlin.chain.ext

import com.fasterxml.jackson.databind.ObjectMapper
import ehn.techiop.hcert.data.Eudgc
import ehn.techiop.hcert.kotlin.chain.Base45Service
import ehn.techiop.hcert.kotlin.chain.CborService
import ehn.techiop.hcert.kotlin.chain.Chain
import ehn.techiop.hcert.kotlin.chain.ChainResult
import ehn.techiop.hcert.kotlin.chain.CompressorService
import ehn.techiop.hcert.kotlin.chain.ContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.CoseService
import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.SampleData
import ehn.techiop.hcert.kotlin.chain.asBase64
import ehn.techiop.hcert.kotlin.chain.faults.BrokenCoseService
import ehn.techiop.hcert.kotlin.chain.faults.FaultyBase45Service
import ehn.techiop.hcert.kotlin.chain.faults.FaultyCompressorService
import ehn.techiop.hcert.kotlin.chain.faults.NonVerifiableCoseService
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

    private val eudgcVac = ObjectMapper().readValue(SampleData.vaccination, Eudgc::class.java)
    private val clock = Clock.fixed(Instant.parse("2021-05-03T18:00:00Z"), ZoneId.systemDefault())
    private val cryptoService = RandomEcKeyCryptoService(clock = clock)

    @Test
    fun writeGen01Vaccination() {
        // TODO Would also need to specify validity, country code!
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcVac)

        createGenerationTestCaseJson(
            clock, eudgcVac, result, "Success, vaccination", "gentestcase01",
            TestExpectedResults(
                verifySchemaGeneration = true,
                verifyEncodeGeneration = true,
            )
        )
    }

    @Test
    fun writeGen02Recovery() {
        val eudgc = ObjectMapper().readValue(SampleData.recovery, Eudgc::class.java)
        val chain = ChainBuilder.good(clock, cryptoService).build()
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
        val chain = ChainBuilder.good(clock, cryptoService).build()
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
        val chain = ChainBuilder.good(clock, cryptoService).build()
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
        val chain = ChainBuilder.good(clock, cryptoService).build()
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
    fun write03Expired() {
        val eudgc = ObjectMapper().readValue(SampleData.vaccination, Eudgc::class.java)
        val clockInPast = Clock.fixed(Instant.parse("2018-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clockInPast)
        val chain = ChainBuilder.good(clockInPast, cryptoService).build()
        val result = chain.encode(eudgc)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgc, result, cryptoService.getCertificate(), qrCode,
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
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).also { it.shuffle() }.asBase64()

        createVerificationTestCaseJson(
            clock, eudgcVac, result, cryptoService.getCertificate(), qrCode,
            "QR code cannot be read", "testcaseQ1",
            TestExpectedResults(
                verifyQrDecode = false,
            )
        )
    }

    // TODO Q2 How to generate a QR Code with a wrong encoding? Our library picks the best mode available
    // TODO Q3 How to generate a QR Code over the maximum size? What content to put in there?

    @Test
    fun writeH1ContextInvalid() {
        val chain = ChainBuilder.good(clock, cryptoService).with(DefaultContextIdentifierService("HL0:"))
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcVac, result, cryptoService.getCertificate(), qrCode,
            "Context identifier does not match schema (HL0:)", "testcaseH1",
            TestExpectedResults(
                verifyQrDecode = true,
                verifyPrefix = false,
            )
        )
    }

    @Test
    fun writeH2ContextInvalid() {
        val chain = ChainBuilder.good(clock, cryptoService).with(DefaultContextIdentifierService("HC2:"))
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcVac, result, cryptoService.getCertificate(), qrCode,
            "Context identifier not supported (HC2:)", "testcaseH2",
            TestExpectedResults(
                verifyQrDecode = true,
                verifyPrefix = false,
            )
        )
    }

    @Test
    fun writeB1Base45Invalid() {
        val chain = ChainBuilder.good(clock, cryptoService).with(FaultyBase45Service())
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcVac, result, cryptoService.getCertificate(), qrCode,
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
        val chain = ChainBuilder.good(clock, cryptoService).with(FaultyCompressorService())
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcVac, result, cryptoService.getCertificate(), qrCode,
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
        val chain = ChainBuilder.good(clock, cryptoService).with(NoopCompressorService())
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcVac, result, cryptoService.getCertificate(), qrCode,
            "Compression omitted", "testcaseZ2",
            TestExpectedResults(
                verifyQrDecode = true,
                verifyPrefix = true,
                verifyBase45Decode = true,
                verifyCompression = false
            )
        )
    }

    // TODO CO1 is this case obsoleted by CO10, CO11, CO12, CO13?
    // TODO CO2 is that really different from CO3?
    // TODO CO3 Seems that the COSE Library does not support EdDSA keys fully ... at least not from Bouncycastle

    @Test
    fun writeCO4KidNotKnown() {
        val chain = ChainBuilder.good(clock, cryptoService).with(NonVerifiableCoseService(cryptoService))
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcVac, result, cryptoService.getCertificate(), qrCode,
            "KID not found in trust list", "testcaseCO4",
            TestExpectedResults(
                verifyQrDecode = true,
                verifyPrefix = true,
                verifyBase45Decode = true,
                verifyCompression = true,
                verifyCoseSignature = false,
            )
        )
    }

    @Test
    fun writeCO5SignatureBroken() {
        val chain = ChainBuilder.good(clock, cryptoService).with(BrokenCoseService(cryptoService))
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcVac, result, cryptoService.getCertificate(), qrCode,
            "Signature broken", "testcaseCO5",
            TestExpectedResults(
                verifyQrDecode = true,
                verifyPrefix = true,
                verifyBase45Decode = true,
                verifyCompression = true,
                verifyCoseSignature = false,
            )
        )
    }

    // TODO CO6 OID combinations ...
    // TODO CO7 split up into different cases?

    data class ChainBuilder(
        val cborService: CborService,
        val coseService: CoseService,
        val contextIdentifierService: ContextIdentifierService,
        val compressorService: CompressorService,
        val base45Service: Base45Service
    ) {
        companion object {
            fun good(clock: Clock, cryptoService: RandomEcKeyCryptoService) = ChainBuilder(
                DefaultCborService(clock = clock),
                DefaultCoseService(cryptoService),
                DefaultContextIdentifierService(),
                DefaultCompressorService(),
                DefaultBase45Service()
            )
        }

        fun with(compressorService: CompressorService) =
            Chain(cborService, coseService, contextIdentifierService, compressorService, base45Service)

        fun with(base45Service: Base45Service) =
            Chain(cborService, coseService, contextIdentifierService, compressorService, base45Service)

        fun with(contextIdentifierService: ContextIdentifierService) =
            Chain(cborService, coseService, contextIdentifierService, compressorService, base45Service)

        fun build() =
            Chain(cborService, coseService, contextIdentifierService, compressorService, base45Service)

        fun with(cryptoService: CryptoService) =
            Chain(
                cborService,
                DefaultCoseService(cryptoService),
                contextIdentifierService,
                compressorService,
                base45Service
            )

        fun with(coseService: CoseService) =
            Chain(cborService, coseService, contextIdentifierService, compressorService, base45Service)

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
