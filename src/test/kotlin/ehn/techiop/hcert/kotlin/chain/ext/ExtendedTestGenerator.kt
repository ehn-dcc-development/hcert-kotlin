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
import ehn.techiop.hcert.kotlin.chain.faults.BothProtectedWrongCoseService
import ehn.techiop.hcert.kotlin.chain.faults.BothUnprotectedWrongCoseService
import ehn.techiop.hcert.kotlin.chain.faults.BrokenCoseService
import ehn.techiop.hcert.kotlin.chain.faults.DuplicateHeaderCoseService
import ehn.techiop.hcert.kotlin.chain.faults.FaultyBase45Service
import ehn.techiop.hcert.kotlin.chain.faults.FaultyCborService
import ehn.techiop.hcert.kotlin.chain.faults.FaultyCompressorService
import ehn.techiop.hcert.kotlin.chain.faults.FaultyCoseService
import ehn.techiop.hcert.kotlin.chain.faults.NoopCompressorService
import ehn.techiop.hcert.kotlin.chain.faults.NoopContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.faults.UnprotectedCoseService
import ehn.techiop.hcert.kotlin.chain.faults.WrongUnprotectedCoseService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultTwoDimCodeService
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.RandomRsaKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.toHexString
import ehn.techiop.hcert.kotlin.trust.ContentType
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
    private val eudgcRec = ObjectMapper().readValue(SampleData.recovery, Eudgc::class.java)
    private val eudgcTest = ObjectMapper().readValue(SampleData.testNaa, Eudgc::class.java)
    private val eudgcTestRat = ObjectMapper().readValue(SampleData.testRat, Eudgc::class.java)
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
    fun writeH3ContextMissing() {
        val chain = ChainBuilder.good(clock, cryptoService).with(NoopContextIdentifierService())
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcVac, result, cryptoService.getCertificate(), qrCode,
            "Context identifier missing", "testcaseH3",
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
                verifyCompression = false
            )
        )
    }

    @Test
    fun writeCO1Rsa2048() {
        val cryptoService = RandomRsaKeyCryptoService(2048, clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcVac, result, cryptoService.getCertificate(), qrCode,
            "All good: RSA 2048 key", "testcaseCO1",
            TestExpectedResults(
                verifyQrDecode = true,
                verifyPrefix = true,
                verifyBase45Decode = true,
                verifyCompression = true,
                verifyCoseSignature = true,
                verifyCborDecode = true,
                verifyJson = true,
            )
        )
    }

    @Test
    fun writeCO2Rsa3072() {
        val cryptoService = RandomRsaKeyCryptoService(3072, clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcVac, result, cryptoService.getCertificate(), qrCode,
            "All good: RSA 3072 key", "testcaseCO2",
            TestExpectedResults(
                verifyQrDecode = true,
                verifyPrefix = true,
                verifyBase45Decode = true,
                verifyCompression = true,
                verifyCoseSignature = true,
                verifyCborDecode = true,
                verifyJson = true,
            )
        )
    }

    @Test
    fun writeCO3Ec256() {
        val cryptoService = RandomEcKeyCryptoService(256, clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcVac, result, cryptoService.getCertificate(), qrCode,
            "All good: EC 256 key", "testcaseCO3",
            TestExpectedResults(
                verifyQrDecode = true,
                verifyPrefix = true,
                verifyBase45Decode = true,
                verifyCompression = true,
                verifyCoseSignature = true,
                verifyCborDecode = true,
                verifyJson = true,
            )
        )
    }

    // TODO CO4 Seems that the COSE Library does not support EdDSA keys fully ... at least not from Bouncycastle

    @Test
    fun writeCO5SignatureBroken() {
        val chain = ChainBuilder.good(clock, cryptoService).with(BrokenCoseService(cryptoService))
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcVac, result, cryptoService.getCertificate(), qrCode,
            "COSE Signature broken", "testcaseCO5",
            TestExpectedResults(
                verifyCoseSignature = false,
            )
        )
    }

    @Test
    fun writeCO6CertTestDgcVac() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.TEST), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcVac, result, cryptoService.getCertificate(), qrCode,
            "OID for Test present, but DGC for vacc", "testcaseCO6",
            TestExpectedResults(
                verifyContentType = false
            )
        )
    }

    @Test
    fun writeCO7CertTestDgcRec() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.TEST), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcRec)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcRec, result, cryptoService.getCertificate(), qrCode,
            "OID for Test present, but DGC for recovery", "testcaseCO7",
            TestExpectedResults(
                verifyContentType = false
            )
        )
    }

    @Test
    fun writeCO8CertVaccDgcTest() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.VACCINATION), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcTest)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcTest, result, cryptoService.getCertificate(), qrCode,
            "OID for Vacc present, but DGC for test", "testcaseCO8",
            TestExpectedResults(
                verifyContentType = false
            )
        )
    }

    @Test
    fun writeCO9CertVaccDgcRecovery() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.VACCINATION), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcRec)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcRec, result, cryptoService.getCertificate(), qrCode,
            "OID for Vacc present, but DGC for recovery", "testcaseCO9",
            TestExpectedResults(
                verifyContentType = false
            )
        )
    }

    @Test
    fun writeC10CertRecoveryDgcVacc() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.RECOVERY), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcVac, result, cryptoService.getCertificate(), qrCode,
            "OID for Recovery present, but DGC for vacc", "testcaseCO10",
            TestExpectedResults(
                verifyContentType = false
            )
        )
    }

    @Test
    fun writeC11CertRecoveryDgcTest() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.RECOVERY), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcTest)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcTest, result, cryptoService.getCertificate(), qrCode,
            "OID for Recovery present, but DGC for test", "testcaseCO11",
            TestExpectedResults(
                verifyContentType = false
            )
        )
    }

    @Test
    fun writeC12CertTestDgcTest() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.TEST), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcTest)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcTest, result, cryptoService.getCertificate(), qrCode,
            "OID for Test present, DGC is test", "testcaseCO12",
            TestExpectedResults(
                verifyContentType = true
            )
        )
    }

    @Test
    fun writeC13CertVaccDgcVacc() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.VACCINATION), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcVac, result, cryptoService.getCertificate(), qrCode,
            "OID for Vacc present, DGC is vacc", "testcaseCO13",
            TestExpectedResults(
                verifyContentType = true
            )
        )
    }

    @Test
    fun writeC14CertRecDgcRec() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.RECOVERY), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcRec)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcRec, result, cryptoService.getCertificate(), qrCode,
            "OID for Recovery present, DGC is recovery", "testcaseCO14",
            TestExpectedResults(
                verifyContentType = true
            )
        )
    }

    @Test
    fun writeC15CertNoneDgcRec() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcRec)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcRec, result, cryptoService.getCertificate(), qrCode,
            "no OID present, DGC is recovery", "testcaseCO15",
            TestExpectedResults(
                verifyContentType = true
            )
        )
    }

    @Test
    fun writeCO16ValidationClockBeforeIssuedAt() {
        val clockInFuture = Clock.fixed(Instant.parse("2023-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clockInFuture)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcTest)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcTest, result, cryptoService.getCertificate(), qrCode,
            "validation clock before \"ISSUED AT\"", "testcaseCO16",
            TestExpectedResults(
                verifyExpirationTime = false
            )
        )
    }

    @Test
    fun writeCO17ValidationClockAfterExpired() {
        val clockInPast = Clock.fixed(Instant.parse("2018-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clockInPast)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcTest)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcTest, result, cryptoService.getCertificate(), qrCode,
            "validation clock after \"expired\"", "testcaseCO17",
            TestExpectedResults(
                verifyExpirationTime = false
            )
        )
    }

    @Test
    fun writeCO18KidValid() {
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcTest)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcTest, result, cryptoService.getCertificate(), qrCode,
            "KID in protected header correct, KID in unprotected header not present", "testcaseCO18",
            TestExpectedResults(
                verifyCoseSignature = true
            )
        )
    }

    @Test
    fun writeCO19KidUnprotected() {
        val chain = ChainBuilder.good(clock, cryptoService).with(UnprotectedCoseService(cryptoService))
        val result = chain.encode(eudgcTest)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcTest, result, cryptoService.getCertificate(), qrCode,
            "KID in protected header not present, KID in unprotected header correct", "testcaseCO19",
            TestExpectedResults(
                verifyCoseSignature = true
            )
        )
    }

    @Test
    fun writeCO20KidInBothHeaders() {
        val chain = ChainBuilder.good(clock, cryptoService).with(DuplicateHeaderCoseService(cryptoService))
        val result = chain.encode(eudgcTest)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcTest, result, cryptoService.getCertificate(), qrCode,
            "KID in protected header correct, KID in unprotected header correct", "testcaseCO20",
            TestExpectedResults(
                verifyCoseSignature = true
            )
        )
    }

    @Test
    fun writeCO21KidInBothHeadersUnprotectedWrong() {
        val chain = ChainBuilder.good(clock, cryptoService).with(BothUnprotectedWrongCoseService(cryptoService))
        val result = chain.encode(eudgcTest)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcTest, result, cryptoService.getCertificate(), qrCode,
            "KID in protected header correct, KID in unprotected header not correct", "testcaseCO21",
            TestExpectedResults(
                verifyCoseSignature = true
            )
        )
    }

    @Test
    fun writeCO22KidProtectedWrongUnprotectedCorrect() {
        val chain = ChainBuilder.good(clock, cryptoService).with(BothProtectedWrongCoseService(cryptoService))
        val result = chain.encode(eudgcTest)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcTest, result, cryptoService.getCertificate(), qrCode,
            "KID in protected header not correct, KID in unprotected header correct", "testcaseCO22",
            TestExpectedResults(
                verifyCoseSignature = false
            )
        )
    }

    @Test
    fun writeCO23KidProtectedNotPresentUnprotectedWrong() {
        val chain = ChainBuilder.good(clock, cryptoService).with(WrongUnprotectedCoseService(cryptoService))
        val result = chain.encode(eudgcTest)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcTest, result, cryptoService.getCertificate(), qrCode,
            "KID in protected header not present, KID in unprotected header not correct", "testcaseCO23",
            TestExpectedResults(
                verifyCoseSignature = false
            )
        )
    }

    @Test
    fun writeCBO1WrongCborStructure() {
        val chain = ChainBuilder.good(clock, cryptoService).with(FaultyCborService())
        val result = chain.encode(eudgcTest)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcTest, result, cryptoService.getCertificate(), qrCode,
            "wrong CBOR structure", "testcaseCBO1",
            TestExpectedResults(
                verifyCborDecode = false
            )
        )
    }

    @Test
    fun writeCBO2WrongCwtStructure() {
        val chain = ChainBuilder.good(clock, cryptoService).with(FaultyCoseService(cryptoService))
        val result = chain.encode(eudgcTest)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcTest, result, cryptoService.getCertificate(), qrCode,
            "wrong CWT structure", "testcaseCBO2",
            TestExpectedResults(
                verifyCoseSignature = false
            )
        )
    }

    @Test
    fun writeDGC1Wrong() {
        val wrong = """
        {
            "ver": "1.0.0",
            "nam": {
            }
        }
        """.trimIndent()
        val eudgcWrong = ObjectMapper().readValue(wrong, Eudgc::class.java)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcWrong)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcWrong, result, cryptoService.getCertificate(), qrCode,
            "DGC does not adhere to schema", "testcaseDGC1",
            TestExpectedResults(
                verifySchemaValidation = false
            )
        )
    }

    @Test
    fun writeDGC2Wrong() {
        val eudgcWrong = ObjectMapper().readValue(SampleData.recovery, Eudgc::class.java)
        eudgcWrong.t = eudgcTest.t
        eudgcWrong.v = eudgcVac.v
        eudgcWrong.r = eudgcRec.r
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcWrong)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcWrong, result, cryptoService.getCertificate(), qrCode,
            "DGC adheres to schema but contains multiple certificates", "testcaseDGC2",
            TestExpectedResults(
                verifySchemaValidation = false
            )
        )
    }

    @Test
    fun writeDGC3Test1() {
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcTest)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcTest, result, cryptoService.getCertificate(), qrCode,
            "correct test1 DGC", "testcaseDGC3",
            TestExpectedResults(
                verifySchemaValidation = true
            )
        )
    }

    @Test
    fun writeDGC4Test2() {
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcTestRat)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcTestRat, result, cryptoService.getCertificate(), qrCode,
            "correct test2 DGC", "testcaseDGC4",
            TestExpectedResults(
                verifySchemaValidation = true
            )
        )
    }

    @Test
    fun writeDGC5Recovery() {
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcRec)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcRec, result, cryptoService.getCertificate(), qrCode,
            "correct recovery DGC", "testcaseDGC5",
            TestExpectedResults(
                verifySchemaValidation = true
            )
        )
    }

    @Test
    fun writeDGC6Recovery() {
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).asBase64()

        createVerificationTestCaseJson(
            clock, eudgcVac, result, cryptoService.getCertificate(), qrCode,
            "correct vacc DGC", "testcaseDGC6",
            TestExpectedResults(
                verifySchemaValidation = true
            )
        )
    }

    data class ChainBuilder(
        val cborService: CborService,
        val coseService: CoseService,
        val contextIdentifierService: ContextIdentifierService,
        val compressorService: CompressorService,
        val base45Service: Base45Service
    ) {
        companion object {
            fun good(clock: Clock, cryptoService: CryptoService) = ChainBuilder(
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

        fun with(cryptoService: CryptoService) = Chain(
            cborService,
            DefaultCoseService(cryptoService),
            contextIdentifierService,
            compressorService,
            base45Service
        )

        fun with(coseService: CoseService) =
            Chain(cborService, coseService, contextIdentifierService, compressorService, base45Service)

        fun with(cborService: CborService) =
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
