package ehn.techiop.hcert.kotlin.chain.ext

import ehn.techiop.hcert.kotlin.chain.*
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
import ehn.techiop.hcert.kotlin.chain.impl.*
import ehn.techiop.hcert.kotlin.crypto.CertificateAdapter
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.trust.ContentType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File


@Disabled("Don't want to generate test case files every time")
class ExtendedTestGenerator {

    private val eudgcVac = Json.decodeFromString<GreenCertificate>(SampleData.vaccination)
    private val eudgcRec = Json.decodeFromString<GreenCertificate>(SampleData.recovery)
    private val eudgcTest = Json.decodeFromString<GreenCertificate>(SampleData.testNaa)
    private val eudgcTestRat = Json.decodeFromString<GreenCertificate>(SampleData.testRat)
    private val clock = FixedClock(Instant.parse("2021-05-03T18:00:00Z"))
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
        val eudgc = Json.decodeFromString<GreenCertificate>(SampleData.recovery)
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
        val eudgc = Json.decodeFromString<GreenCertificate>(SampleData.testNaa)
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
        val eudgc = Json.decodeFromString<GreenCertificate>(SampleData.testRat)
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
            "INVALID: QR code cannot be read", "Q1",
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
            "INVALID: Context does not match schema (HL0:)", "H1",
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
            "INVALID: Context value not supported (HC2:)", "H2",
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
            "INVALID: Context value missing (only BASE45 encoding)", "H3",
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
            "INVALID: BASE45 invalid (wrong encoding characters)", "B1",
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
            "INVALID: Compression broken", "Z1",
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
            "INVALID: Not compressed", "Z2",
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
            "VALID: RSA 2048 key", "CO1",
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
            "VALID: RSA 3072 key", "CO2",
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
            "VALID: EC 256 key", "CO3",
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
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: Signature cryptographically invalid", "CO5",
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
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: OID for Test present, but DGC for vacc", "CO6",
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
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: OID for Test present, but DGC for recovery", "CO7",
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
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: OID for Vacc present, but DGC for test", "CO8",
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
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: OID for Vacc present, but DGC for recovery", "CO9",
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
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: OID for Recovery present, but DGC for vacc", "CO10",
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
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: OID for Recovery present, but DGC for test", "CO11",
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
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: OID for Test present, DGC is test", "CO12",
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
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: OID for Vacc present, DGC is vacc", "CO13",
            TestExpectedResults(
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
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: OID for Recovery present, DGC is recovery", "CO14",
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
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: no OID present, DGC is recovery", "CO15",
            TestExpectedResults(
                keyUsage = true
            )
        )
    }

    @Test
    fun writeCO16ValidationClockBeforeIssuedAt() {
        val clockInFuture = FixedClock(Instant.parse("2023-05-03T18:00:00Z"))
        val cryptoService = RandomEcKeyCryptoService(clock = clockInFuture)
        val chain = ChainBuilder.good(clockInFuture, cryptoService).build()
        val result = chain.encode(eudgcTest)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: validation clock before \"ISSUED AT\"", "CO16",
            TestExpectedResults(
                expirationCheck = false
            )
        )
    }

    @Test
    fun writeCO17ValidationClockAfterExpired() {
        val clockInPast = FixedClock(Instant.parse("2018-05-03T18:00:00Z"))
        val cryptoService = RandomEcKeyCryptoService(clock = clockInPast)
        val chain = ChainBuilder.good(clockInPast, cryptoService).build()
        val result = chain.encode(eudgcTest)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: validation clock after \"expired\"", "CO17",
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
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: KID in protected header correct, KID in unprotected header not present", "CO18",
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
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: KID in protected header not present, KID in unprotected header correct", "CO19",
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
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: KID in protected header correct, KID in unprotected header correct", "CO20",
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
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "VALID: KID in protected header correct, KID in unprotected header not correct", "CO21",
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
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: KID in protected header not correct, KID in unprotected header correct", "CO22",
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
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: KID in protected header not present, KID in unprotected header not correct", "CO23",
            TestExpectedResults(
                coseSignature = false
            )
        )
    }

    // TODO Also test wrong CWT structure

    @Test
    fun writeCBO1WrongCborStructure() {
        val chain = ChainBuilder.good(clock, cryptoService).with(FaultyCborService())
        val result = chain.encode(eudgcTest)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                greenCertificate = eudgcTest,
                cborHex = result.step0Cbor.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: wrong CBOR structure", "CBO1",
            TestExpectedResults(
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
                greenCertificate = eudgcTest,
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: wrong CWT structure", "CBO2",
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
        val eudgcWrong = Json.decodeFromString<GreenCertificate>(wrong)
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcWrong)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                greenCertificate = eudgcWrong,
                cborHex = result.step0Cbor.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: DGC does not adhere to schema", "DGC1",
            TestExpectedResults(
                schemaValidation = false
            )
        )
    }

    @Test
    fun writeDGC2Wrong() {
        val eudgcWrong = Json.decodeFromString<GreenCertificate>(SampleData.recovery)
        // TODO
        //eudgcWrong.tests = eudgcTest.tests
        //eudgcWrong.vaccinations = eudgcVac.vaccinations
        //eudgcWrong.recoveryStatements = eudgcRec.recoveryStatements
        val chain = ChainBuilder.good(clock, cryptoService).build()
        val result = chain.encode(eudgcWrong)

        createVerificationTestCaseJson(
            clock, cryptoService.getCertificate(),
            ChainResultAdapter(
                greenCertificate = eudgcWrong,
                cborHex = result.step0Cbor.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: DGC adheres to schema but contains multiple certificates", "DGC2",
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
                greenCertificate = eudgcTest,
                cborHex = result.step0Cbor.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: correct test1 DGC", "DGC3",
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
                greenCertificate = eudgcTestRat,
                cborHex = result.step0Cbor.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: correct test2 DGC", "DGC4",
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
                greenCertificate = eudgcRec,
                cborHex = result.step0Cbor.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: correct recovery DGC", "DGC5",
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
                greenCertificate = eudgcVac,
                cborHex = result.step0Cbor.toHexString(),
                base45WithPrefix = result.step5Prefixed
            ),
            "INVALID: correct vacc DGC", "DGC6",
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
        val base45Service: Base45Service,
        val schemaValidationService: SchemaValidationService,
        val higherOrderValidationService: HigherOrderValidationService
    ) {
        companion object {
            fun good(clock: Clock, cryptoService: CryptoService) = ChainBuilder(
                DefaultCborService(),
                DefaultCwtService(clock = clock),
                DefaultCoseService(cryptoService),
                DefaultContextIdentifierService(),
                DefaultCompressorService(),
                DefaultBase45Service(),
                DefaultSchemaValidationService(),
                DefaultHigherOrderValidationService()
            )
        }

        fun with(compressorService: CompressorService) =
            Chain(
                higherOrderValidationService,
                schemaValidationService,
                cborService,
                cwtService,
                coseService,
                compressorService,
                base45Service,
                contextIdentifierService
            )

        fun with(base45Service: Base45Service) =
            Chain(
                higherOrderValidationService,
                schemaValidationService,
                cborService,
                cwtService,
                coseService,
                compressorService,
                base45Service,
                contextIdentifierService
            )

        fun with(contextIdentifierService: ContextIdentifierService) =
            Chain(
                higherOrderValidationService,
                schemaValidationService,
                cborService,
                cwtService,
                coseService,
                compressorService,
                base45Service,
                contextIdentifierService
            )

        fun build() =
            Chain(
                higherOrderValidationService,
                schemaValidationService,
                cborService,
                cwtService,
                coseService,
                compressorService,
                base45Service,
                contextIdentifierService
            )

        fun with(cryptoService: CryptoService) = Chain(
            higherOrderValidationService,
            schemaValidationService,
            cborService,
            cwtService,
            DefaultCoseService(cryptoService),
            compressorService,
            base45Service,
            contextIdentifierService
        )

        fun with(coseService: CoseService) =
            Chain(
                higherOrderValidationService,
                schemaValidationService,
                cborService,
                cwtService,
                coseService,
                compressorService,
                base45Service,
                contextIdentifierService
            )

        fun with(cwtService: CwtService) =
            Chain(
                higherOrderValidationService,
                schemaValidationService,
                cborService,
                cwtService,
                coseService,
                compressorService,
                base45Service,
                contextIdentifierService
            )

        fun with(cborService: CborService) =
            Chain(
                higherOrderValidationService,
                schemaValidationService,
                cborService,
                cwtService,
                coseService,
                compressorService,
                base45Service,
                contextIdentifierService
            )

    }

    data class ChainResultAdapter(
        val greenCertificate: GreenCertificate? = null,
        val cborHex: String? = null,
        val coseHex: String? = null,
        val compressedHex: String? = null,
        val base45: String? = null,
        val base45WithPrefix: String? = null,
        val qrCode: String? = null,
    ) {
        companion object {
            fun from(eudgc: GreenCertificate, result: ChainResult) = ChainResultAdapter(
                greenCertificate = eudgc,
                cborHex = result.step0Cbor.toHexString(),
                coseHex = result.step2Cose.toHexString(),
                compressedHex = result.step3Compressed.toHexString(),
                base45 = result.step4Encoded,
                base45WithPrefix = result.step5Prefixed,
            )
        }
    }

    private fun createVerificationTestCaseJson(
        clock: Clock,
        certificate: CertificateAdapter,
        result: ChainResultAdapter,
        description: String,
        filename: String,
        expectedResult: TestExpectedResults,
    ) {
        val context = TestContext(
            version = 1,
            schema = "1.0.0",
            certificate = certificate.encoded.asBase64(),
            validationClock = clock.now(),
            description = description
        )
        val testcase = TestCase(
            eudgc = result.greenCertificate,
            cborHex = result.cborHex,
            coseHex = result.coseHex,
            compressedHex = result.compressedHex,
            base45 = result.base45,
            base45WithPrefix = result.base45WithPrefix,
            qrCodePng = result.qrCode,
            context = context,
            expectedResult = expectedResult
        )
        File("src/test/resources/dgc-testdata/common/2DCode/raw/$filename.json").bufferedWriter().use {
            it.write(Json { prettyPrint = true }.encodeToString(testcase))
        }
    }

    private fun createGenerationTestCaseJson(
        clock: Clock,
        greenCertificate: GreenCertificate,
        result: ChainResult,
        description: String,
        filename: String,
        expectedResult: TestExpectedResults,
    ) {
        val context = TestContext(
            version = 1,
            schema = "1.0.0",
            certificate = null,
            validationClock = clock.now(),
            description = description
        )
        val testcase = TestCase(
            eudgc = greenCertificate,
            cborHex = result.step0Cbor.toHexString(),
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
