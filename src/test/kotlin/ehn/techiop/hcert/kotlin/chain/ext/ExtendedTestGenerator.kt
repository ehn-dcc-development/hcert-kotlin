package ehn.techiop.hcert.kotlin.chain.ext

import com.fasterxml.jackson.databind.ObjectMapper
import ehn.techiop.hcert.data.Eudgc
import ehn.techiop.hcert.kotlin.chain.Base45Service
import ehn.techiop.hcert.kotlin.chain.CborService
import ehn.techiop.hcert.kotlin.chain.CwtService
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
import ehn.techiop.hcert.kotlin.chain.faults.FaultyCwtService
import ehn.techiop.hcert.kotlin.chain.faults.FaultyCompressorService
import ehn.techiop.hcert.kotlin.chain.faults.FaultyCoseService
import ehn.techiop.hcert.kotlin.chain.faults.NoopCompressorService
import ehn.techiop.hcert.kotlin.chain.faults.NoopContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.faults.UnprotectedCoseService
import ehn.techiop.hcert.kotlin.chain.faults.WrongUnprotectedCoseService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCwtService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCoseService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultTwoDimCodeService
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.RandomRsaKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.toHexString
import ehn.techiop.hcert.kotlin.data.GreenCertificate
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
                schemaGeneration = true,
                encodeGeneration = true,
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
                schemaGeneration = true,
                encodeGeneration = true,
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
                schemaGeneration = true,
                encodeGeneration = true,
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
                schemaGeneration = true,
                encodeGeneration = true,
            )
        )
    }

    @Test
    fun writeQ1QrCodeBroken() {
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcVac)
        val qrCode = DefaultTwoDimCodeService(350).encode(result.step5Prefixed).also { it.shuffle() }.asBase64()

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(base45WithPrefix = result.step5Prefixed, qrCode = qrCode),
            "INVALID: QR code cannot be read", "testcaseQ1",
            TestExpectedResults(
                qrDecode = false,
            )
        )
    }

    // TODO Q2 How to generate a QR Code with a wrong encoding? Our library picks the best mode available

    @Test
    fun writeH1ContextInvalid() {
        val chain = ChainBuilder.good(clock, cryptoService).with(DefaultContextIdentifierService("HL0:"))
        val result = chain.encode(eudgcVac)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(base45 = result.step4Encoded, base45WithPrefix = result.step5Prefixed),
            "INVALID: Context does not match schema (HL0:)", "testcaseH1",
            TestExpectedResults(
                prefix = false,
            )
        )
    }

    @Test
    fun writeH2ContextInvalid() {
        val chain = ChainBuilder.good(clock, cryptoService).with(DefaultContextIdentifierService("HC2:"))
        val result = chain.encode(eudgcVac)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(base45 = result.step4Encoded, base45WithPrefix = result.step5Prefixed),
            "INVALID: Context value not supported (HC2:)", "testcaseH2",
            TestExpectedResults(
                prefix = false,
            )
        )
    }

    @Test
    fun writeH3ContextMissing() {
        val chain = ChainBuilder.good(clock, cryptoService).with(NoopContextIdentifierService())
        val result = chain.encode(eudgcVac)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(base45 = result.step4Encoded, base45WithPrefix = result.step5Prefixed),
            "INVALID: Context value missing (only BASE45 encoding)", "testcaseH3",
            TestExpectedResults(
                prefix = false,
            )
        )
    }

    @Test
    fun writeB1Base45Invalid() {
        val chain = ChainBuilder.good(clock, cryptoService).with(FaultyBase45Service())
        val result = chain.encode(eudgcVac)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                compressedHex = result.step3Compressed.toHexString(),
                base45 = result.step4Encoded,
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: BASE45 invalid (wrong encoding characters)", "testcaseB1",
            TestExpectedResults(
                qrDecode = true,
                prefix = true,
                base45Decode = false
            )
        )
    }

    @Test
    fun writeZ1CompressionInvalid() {
        val chain = ChainBuilder.good(clock, cryptoService).with(FaultyCompressorService())
        val result = chain.encode(eudgcVac)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            // todo base45withprefix for input!
            ChainResultAdapter(
                coseHex = result.step2Cose.toHexString(),
                compressedHex = result.step3Compressed.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: Compression broken", "testcaseZ1",
            TestExpectedResults(
                compression = false
            )
        )
    }

    @Test
    fun writeZ2CompressionNone() {
        val chain = ChainBuilder.good(clock, cryptoService).with(NoopCompressorService())
        val result = chain.encode(eudgcVac)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                coseHex = result.step2Cose.toHexString(),
                compressedHex = result.step3Compressed.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: Not compressed", "testcaseZ2",
            TestExpectedResults(
                compression = false
            )
        )
    }

    @Test
    fun writeCO1Rsa2048() {
        val cryptoService = RandomRsaKeyCryptoService(2048, clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcVac)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter.from(eudgcVac, result),
            "VALID: RSA 2048 key", "testcaseCO1",
            TestExpectedResults(
                prefix = true,
                base45Decode = true,
                compression = true,
                coseSignature = true,
                cborDecode = true,
                json = true,
            )
        )
    }

    @Test
    fun writeCO2Rsa3072() {
        val cryptoService = RandomRsaKeyCryptoService(3072, clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcVac)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter.from(eudgcVac, result),
            "VALID: RSA 3072 key", "testcaseCO2",
            TestExpectedResults(
                prefix = true,
                base45Decode = true,
                compression = true,
                coseSignature = true,
                cborDecode = true,
                json = true,
            )
        )
    }

    @Test
    fun writeCO3Ec256() {
        val cryptoService = RandomEcKeyCryptoService(256, clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcVac)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter.from(eudgcVac, result),
            "VALID: EC 256 key", "testcaseCO3",
            TestExpectedResults(
                prefix = true,
                base45Decode = true,
                compression = true,
                coseSignature = true,
                cborDecode = true,
                json = true,
            )
        )
    }

    // TODO CO4 Seems that the COSE Library does not support EdDSA keys fully ... at least not from Bouncycastle

    @Test
    fun writeCO5SignatureBroken() {
        val chain = ChainBuilder.good(clock, cryptoService).with(BrokenCoseService(cryptoService))
        val result = chain.encode(eudgcVac)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: Signature cryptographically invalid", "testcaseCO5",
            TestExpectedResults(
                coseSignature = false,
            )
        )
    }

    @Test
    fun writeCO6CertTestDgcVac() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.TEST), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcVac)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: OID for Test present, but DGC for vacc", "testcaseCO6",
            TestExpectedResults(
                keyUsage = false
            )
        )
    }

    @Test
    fun writeCO7CertTestDgcRec() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.TEST), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcRec)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: OID for Test present, but DGC for recovery", "testcaseCO7",
            TestExpectedResults(
                keyUsage = false
            )
        )
    }

    @Test
    fun writeCO8CertVaccDgcTest() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.VACCINATION), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcTest)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: OID for Vacc present, but DGC for test", "testcaseCO8",
            TestExpectedResults(
                keyUsage = false
            )
        )
    }

    @Test
    fun writeCO9CertVaccDgcRecovery() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.VACCINATION), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcRec)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: OID for Vacc present, but DGC for recovery", "testcaseCO9",
            TestExpectedResults(
                keyUsage = false
            )
        )
    }

    @Test
    fun writeC10CertRecoveryDgcVacc() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.RECOVERY), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcVac)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: OID for Recovery present, but DGC for vacc", "testcaseCO10",
            TestExpectedResults(
                keyUsage = false
            )
        )
    }

    @Test
    fun writeC11CertRecoveryDgcTest() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.RECOVERY), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcTest)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: OID for Recovery present, but DGC for test", "testcaseCO11",
            TestExpectedResults(
                keyUsage = false
            )
        )
    }

    @Test
    fun writeC12CertTestDgcTest() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.TEST), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcTest)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: OID for Test present, DGC is test", "testcaseCO12",
            TestExpectedResults(
                keyUsage = true
            )
        )
    }

    @Test
    fun writeC13CertVaccDgcVacc() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.VACCINATION), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcVac)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: OID for Vacc present, DGC is vacc",
            "testcaseCO13", TestExpectedResults(
                keyUsage = true
            )
        )
    }

    @Test
    fun writeC14CertRecDgcRec() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(ContentType.RECOVERY), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcRec)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: OID for Recovery present, DGC is recovery", "testcaseCO14",
            TestExpectedResults(
                keyUsage = true
            )
        )
    }

    @Test
    fun writeC15CertNoneDgcRec() {
        val cryptoService = RandomEcKeyCryptoService(256, contentType = listOf(), clock = clock)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcRec)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: no OID present, DGC is recovery", "testcaseCO15",
            TestExpectedResults(
                keyUsage = true
            )
        )
    }

    @Test
    fun writeCO16ValidationClockBeforeIssuedAt() {
        val clockInFuture = Clock.fixed(Instant.parse("2023-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clockInFuture)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcTest)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: validation clock before \"ISSUED AT\"", "testcaseCO16", TestExpectedResults(
                expirationCheck = false
            )
        )
    }

    @Test
    fun writeCO17ValidationClockAfterExpired() {
        val clockInPast = Clock.fixed(Instant.parse("2018-05-03T18:00:00Z"), ZoneId.systemDefault())
        val cryptoService = RandomEcKeyCryptoService(clock = clockInPast)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcTest)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: validation clock after \"expired\"", "testcaseCO17",
            TestExpectedResults(
                expirationCheck = false
            )
        )
    }

    @Test
    fun writeCO18KidValid() {
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcTest)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: KID in protected header correct, KID in unprotected header not present", "testcaseCO18",
            TestExpectedResults(
                coseSignature = true
            )
        )
    }

    @Test
    fun writeCO19KidUnprotected() {
        val chain = ChainBuilder.good(clock, cryptoService).with(UnprotectedCoseService(cryptoService))
        val result = chain.encode(eudgcTest)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: KID in protected header not present, KID in unprotected header correct", "testcaseCO19",
            TestExpectedResults(
                coseSignature = true
            )
        )
    }

    @Test
    fun writeCO20KidInBothHeaders() {
        val chain = ChainBuilder.good(clock, cryptoService).with(DuplicateHeaderCoseService(cryptoService))
        val result = chain.encode(eudgcTest)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: KID in protected header correct, KID in unprotected header correct", "testcaseCO20",
            TestExpectedResults(
                coseSignature = true
            )
        )
    }

    @Test
    fun writeCO21KidInBothHeadersUnprotectedWrong() {
        val chain = ChainBuilder.good(clock, cryptoService).with(BothUnprotectedWrongCoseService(cryptoService))
        val result = chain.encode(eudgcTest)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: KID in protected header correct, KID in unprotected header not correct", "testcaseCO21",
            TestExpectedResults(
                coseSignature = true
            )
        )
    }

    @Test
    fun writeCO22KidProtectedWrongUnprotectedCorrect() {
        val chain = ChainBuilder.good(clock, cryptoService).with(BothProtectedWrongCoseService(cryptoService))
        val result = chain.encode(eudgcTest)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: KID in protected header not correct, KID in unprotected header correct", "testcaseCO22",
            TestExpectedResults(
                coseSignature = false
            )
        )
    }

    @Test
    fun writeCO23KidProtectedNotPresentUnprotectedWrong() {
        val chain = ChainBuilder.good(clock, cryptoService).with(WrongUnprotectedCoseService(cryptoService))
        val result = chain.encode(eudgcTest)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: KID in protected header not present, KID in unprotected header not correct", "testcaseCO23",
            TestExpectedResults(
                coseSignature = false
            )
        )
    }

    @Test
    fun writeCBO1WrongCborStructure() {
        val chain = ChainBuilder.good(clock, cryptoService).with(FaultyCwtService())
        val result = chain.encode(eudgcTest)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                eudgc = eudgcTest,
                cborHex = result.step1Cwt.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: wrong CBOR structure",
            "testcaseCBO1", TestExpectedResults(
                cborDecode = false
            )
        )
    }

    @Test
    fun writeCBO2WrongCwtStructure() {
        val chain = ChainBuilder.good(clock, cryptoService).with(FaultyCoseService(cryptoService))
        val result = chain.encode(eudgcTest)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                eudgc = eudgcTest,
                cborHex = result.step1Cwt.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: wrong CWT structure", "testcaseCBO2",
            TestExpectedResults(
                coseSignature = false
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

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                eudgc = eudgcWrong,
                cborHex = result.step1Cwt.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: DGC does not adhere to schema", "testcaseDGC1",
            TestExpectedResults(
                schemaValidation = false
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

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                eudgc = eudgcWrong,
                cborHex = result.step1Cwt.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: DGC adheres to schema but contains multiple certificates", "testcaseDGC2",
            TestExpectedResults(
                schemaValidation = false
            )
        )
    }

    @Test
    fun writeDGC3Test1() {
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcTest)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                eudgc = eudgcTest,
                cborHex = result.step1Cwt.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: correct test1 DGC", "testcaseDGC3",
            TestExpectedResults(
                schemaValidation = true
            )
        )
    }

    @Test
    fun writeDGC4Test2() {
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcTestRat)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                eudgc = eudgcTestRat,
                cborHex = result.step1Cwt.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: correct test2 DGC", "testcaseDGC4",
            TestExpectedResults(
                schemaValidation = true
            )
        )
    }

    @Test
    fun writeDGC5Recovery() {
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcRec)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                eudgc = eudgcRec,
                cborHex = result.step1Cwt.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: correct recovery DGC", "testcaseDGC5",
            TestExpectedResults(
                schemaValidation = true
            )
        )
    }

    @Test
    fun writeDGC6Recovery() {
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcVac)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                eudgc = eudgcVac,
                cborHex = result.step1Cwt.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: correct vacc DGC", "testcaseDGC6",
            TestExpectedResults(
                schemaValidation = true
            )
        )
    }

    data class ChainBuilder(
        val cborService: CborService,
        val cwtService: CwtService,
        val coseService: CoseService,
        val contextIdentifierService: ContextIdentifierService,
        val compressorService: CompressorService,
        val base45Service: Base45Service
    ) {
        companion object {
            fun good(clock: Clock, cryptoService: CryptoService) = ChainBuilder(
                DefaultCborService(),
                DefaultCwtService(clock = clock),
                DefaultCoseService(cryptoService),
                DefaultContextIdentifierService(),
                DefaultCompressorService(),
                DefaultBase45Service()
            )
        }

        fun with(compressorService: CompressorService) =
            Chain(cborService, cwtService, coseService, contextIdentifierService, compressorService, base45Service)

        fun with(base45Service: Base45Service) =
            Chain(cborService, cwtService, coseService, contextIdentifierService, compressorService, base45Service)

        fun with(contextIdentifierService: ContextIdentifierService) =
            Chain(cborService, cwtService, coseService, contextIdentifierService, compressorService, base45Service)

        fun build() =
            Chain(cborService, cwtService, coseService, contextIdentifierService, compressorService, base45Service)

        fun with(cryptoService: CryptoService) = Chain(
            cborService,
            cwtService,
            DefaultCoseService(cryptoService),
            contextIdentifierService,
            compressorService,
            base45Service
        )

        fun with(coseService: CoseService) =
            Chain(cborService, cwtService, coseService, contextIdentifierService, compressorService, base45Service)

        fun with(cwtService: CwtService) =
            Chain(cborService, cwtService, coseService, contextIdentifierService, compressorService, base45Service)

    }

    data class ChainResultAdapter(
        val eudgc: Eudgc? = null,
        val cborHex: String? = null,
        val coseHex: String? = null,
        val compressedHex: String? = null,
        val base45: String? = null,
        val base45WithPrefix: String? = null,
        val qrCode: String? = null,
    ) {
        companion object {
            fun from(eudgc: Eudgc, result: ChainResult) = ChainResultAdapter(
                eudgc = eudgc,
                cborHex = result.step1Cwt.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                compressedHex = result.step3Compressed.toHexString(),
                base45 = result.step4Encoded,
                base45WithPrefix = result.step5Prefixed,
            )
        }
    }

    private fun createVerificationTestCaseJson(
        clock: Clock,
        certificate: X509Certificate,
        result: ChainResultAdapter,
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
            eudgc = result.eudgc?.let { GreenCertificate.fromEuSchema(it) },
            cborHex = result.cborHex,
            coseHex = result.coseHex,
            compressedHex = result.compressedHex,
            base45 = result.base45,
            base45WithPrefix = result.base45WithPrefix,
            qrCodePng = result.qrCode,
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
            eudgc = GreenCertificate.fromEuSchema(eudgc),
            cborHex = result.step1Cwt.toHexString(),
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
