package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.RandomRsaKeyCryptoService
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.trust.ContentType
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class SimpleChainTest : FunSpec({

    withData(
        TestInput(SampleData.testRat, ContentType.TEST, VerificationDecision.GOOD),
        TestInput(SampleData.testRat, ContentType.VACCINATION, VerificationDecision.FAIL),
        TestInput(SampleData.testNaa, ContentType.TEST, VerificationDecision.GOOD),
        TestInput(SampleData.testNaa, ContentType.RECOVERY, VerificationDecision.FAIL),
        TestInput(SampleData.vaccination, ContentType.VACCINATION, VerificationDecision.GOOD),
        TestInput(SampleData.vaccination, ContentType.TEST, VerificationDecision.FAIL),
        TestInput(SampleData.recovery, ContentType.RECOVERY, VerificationDecision.GOOD),
        TestInput(SampleData.recovery, ContentType.VACCINATION, VerificationDecision.FAIL)
    ) { input ->

        withData(
            nameFn = { "RSA ${it.keySize}" },
            RandomRsaKeyCryptoService(2048, listOf(input.contentType)),
            RandomRsaKeyCryptoService(3072, listOf(input.contentType))
        ) { cryptoSrv ->
            verify(input.data, cryptoSrv, input.outcome)
        }

        withData(
            nameFn = { "EC ${it.keySize}" },
            RandomEcKeyCryptoService(256, listOf(input.contentType)),
            RandomEcKeyCryptoService(384, listOf(input.contentType))
        ) { cryptoSrv ->
            verify(input.data, cryptoSrv, input.outcome)
        }
    }
})

data class TestInput(val data: String, val contentType: ContentType, val outcome: VerificationDecision)

private fun verify(jsonInput: String, cryptoService: CryptoService, outcome: VerificationDecision) {
    val input = Json.decodeFromString<GreenCertificate>(jsonInput)

    val encodingChain = DefaultChain.buildCreationChain(cryptoService)
    val certificateRepository = PrefilledCertificateRepository(cryptoService.getCertificate())
    val decodingChain = DefaultChain.buildVerificationChain(certificateRepository)

    val output = encodingChain.encode(input)

    val result = decodingChain.decodeExtended(output.step5Prefixed)
    result.chainDecodeResult.eudgc shouldBe input
    result.verificationResult.cborDecoded shouldBe true
    result.decision shouldBe outcome
}
