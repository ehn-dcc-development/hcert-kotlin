package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.SampleData.Companion.recovery
import ehn.techiop.hcert.kotlin.chain.SampleData.Companion.testNaa
import ehn.techiop.hcert.kotlin.chain.SampleData.Companion.testRat
import ehn.techiop.hcert.kotlin.chain.SampleData.Companion.vaccination
import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.crypto.KeyType
import ehn.techiop.hcert.kotlin.crypto.KeyType.EC
import ehn.techiop.hcert.kotlin.crypto.KeyType.RSA
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.trust.ContentType
import ehn.techiop.hcert.kotlin.trust.ContentType.RECOVERY
import ehn.techiop.hcert.kotlin.trust.ContentType.TEST
import ehn.techiop.hcert.kotlin.trust.ContentType.VACCINATION
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.time.Duration

class ContentTypeChainTest : DescribeSpec({

    timeout = Duration.seconds(5).inWholeMilliseconds

    val listOfInput = listOf(
        CTInput(testRat, TEST, EC, 256, TEST),
        CTInput(testRat, TEST, EC, 256, VACCINATION),
        CTInput(testRat, TEST, EC, 256, RECOVERY),
        CTInput(testNaa, TEST, EC, 256, TEST),
        CTInput(testNaa, TEST, EC, 256, VACCINATION),
        CTInput(testNaa, TEST, EC, 256, RECOVERY),
        CTInput(vaccination, VACCINATION, EC, 256, VACCINATION),
        CTInput(vaccination, VACCINATION, EC, 256, TEST),
        CTInput(vaccination, VACCINATION, EC, 256, RECOVERY),
        CTInput(recovery, RECOVERY, EC, 256, RECOVERY),
        CTInput(recovery, RECOVERY, EC, 256, TEST),
        CTInput(recovery, RECOVERY, EC, 256, VACCINATION),

        CTInput(testRat, TEST, RSA, 2048, TEST),
        CTInput(testRat, TEST, RSA, 2048, VACCINATION),
        CTInput(testRat, TEST, RSA, 2048, RECOVERY),
        CTInput(testNaa, TEST, RSA, 2048, TEST),
        CTInput(testNaa, TEST, RSA, 2048, VACCINATION),
        CTInput(testNaa, TEST, RSA, 2048, RECOVERY),
        CTInput(vaccination, VACCINATION, RSA, 2048, VACCINATION),
        CTInput(vaccination, VACCINATION, RSA, 2048, TEST),
        CTInput(vaccination, VACCINATION, RSA, 2048, RECOVERY),
        CTInput(recovery, RECOVERY, RSA, 2048, RECOVERY),
        CTInput(recovery, RECOVERY, RSA, 2048, TEST),
        CTInput(recovery, RECOVERY, RSA, 2048, VACCINATION),
    )

    withData(
        nameFn = { "${it.dataType} vs. ${it.certContentType} ${it.keyType}-${it.keySize}" },
        listOfInput
    ) { input ->
        val service = CryptoServiceHolder.getRandomCryptoService(input.keyType, input.keySize, input.certContentType)
        val dataInput = Json.decodeFromString<GreenCertificate>(input.data)
        val encodingChain = DefaultChain.buildCreationChain(service)
        val certificateRepository = PrefilledCertificateRepository(service.getCertificate())
        val decodingChain = DefaultChain.buildVerificationChain(certificateRepository)

        val output = encodingChain.encode(dataInput)

        val result = decodingChain.decode(output.step5Prefixed)
        val expectedOutcome = input.dataType == input.certContentType
        (result.verificationResult.error == null) shouldBe expectedOutcome
        if (expectedOutcome) // our chain exits early on an error
            result.chainDecodeResult.eudgc shouldBe dataInput
        else
            result.chainDecodeResult.eudgc shouldBe null
    }

})

private data class CTInput(
    val data: String,
    val dataType: ContentType,
    val keyType: KeyType,
    val keySize: Int,
    val certContentType: ContentType,
)
