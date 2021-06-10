package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.RandomRsaKeyCryptoService
import ehn.techiop.hcert.kotlin.crypto.KeyType
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.trust.ContentType
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.time.Duration

class ContentTypeChainTest : DescribeSpec({

    timeout = Duration.seconds(5).inWholeMilliseconds

    val listOfInput = listOf(
        ContentTypeTestInput(SampleData.testRat, KeyType.EC, 256, ContentType.TEST, true),
        ContentTypeTestInput(SampleData.testRat, KeyType.EC, 256, ContentType.VACCINATION, false),
        ContentTypeTestInput(SampleData.testRat, KeyType.EC, 256, ContentType.RECOVERY, false),
        ContentTypeTestInput(SampleData.testNaa, KeyType.EC, 256, ContentType.TEST, true),
        ContentTypeTestInput(SampleData.testNaa, KeyType.EC, 256, ContentType.VACCINATION, false),
        ContentTypeTestInput(SampleData.testNaa, KeyType.EC, 256, ContentType.RECOVERY, false),
        ContentTypeTestInput(SampleData.vaccination, KeyType.EC, 256, ContentType.VACCINATION, true),
        ContentTypeTestInput(SampleData.vaccination, KeyType.EC, 256, ContentType.TEST, false),
        ContentTypeTestInput(SampleData.vaccination, KeyType.EC, 256, ContentType.RECOVERY, false),
        ContentTypeTestInput(SampleData.recovery, KeyType.EC, 256, ContentType.RECOVERY, true),
        ContentTypeTestInput(SampleData.recovery, KeyType.EC, 256, ContentType.TEST, false),
        ContentTypeTestInput(SampleData.recovery, KeyType.EC, 256, ContentType.VACCINATION, false),

        ContentTypeTestInput(SampleData.testRat, KeyType.RSA, 2048, ContentType.TEST, true),
        ContentTypeTestInput(SampleData.testRat, KeyType.RSA, 2048, ContentType.VACCINATION, false),
        ContentTypeTestInput(SampleData.testRat, KeyType.RSA, 2048, ContentType.RECOVERY, false),
        ContentTypeTestInput(SampleData.testNaa, KeyType.RSA, 2048, ContentType.TEST, true),
        ContentTypeTestInput(SampleData.testNaa, KeyType.RSA, 2048, ContentType.VACCINATION, false),
        ContentTypeTestInput(SampleData.testNaa, KeyType.RSA, 2048, ContentType.RECOVERY, false),
        ContentTypeTestInput(SampleData.vaccination, KeyType.RSA, 2048, ContentType.VACCINATION, true),
        ContentTypeTestInput(SampleData.vaccination, KeyType.RSA, 2048, ContentType.TEST, false),
        ContentTypeTestInput(SampleData.vaccination, KeyType.RSA, 2048, ContentType.RECOVERY, false),
        ContentTypeTestInput(SampleData.recovery, KeyType.RSA, 2048, ContentType.RECOVERY, true),
        ContentTypeTestInput(SampleData.recovery, KeyType.RSA, 2048, ContentType.TEST, false),
        ContentTypeTestInput(SampleData.recovery, KeyType.RSA, 2048, ContentType.VACCINATION, false),
    )

    withData(nameFn = { "${it.contentType} ${it.keyType}${it.keySize}" }, listOfInput) { input ->
        val service = if (input.keyType == KeyType.EC)
            RandomEcKeyCryptoService(input.keySize, listOf(input.contentType))
        else
            RandomRsaKeyCryptoService(input.keySize, listOf(input.contentType))
        val dataInput = Json.decodeFromString<GreenCertificate>(input.data)
        val encodingChain = DefaultChain.buildCreationChain(service)
        val certificateRepository = PrefilledCertificateRepository(service.getCertificate())
        val decodingChain = DefaultChain.buildVerificationChain(certificateRepository)

        val output = encodingChain.encode(dataInput)

        val result = decodingChain.decode(output.step5Prefixed)
        (result.verificationResult.error == null) shouldBe input.outcome
        if (input.outcome) // our chain exits early on an error
            result.chainDecodeResult.eudgc shouldBe dataInput
        else
            result.chainDecodeResult.eudgc shouldBe null
    }

})

private data class ContentTypeTestInput(
    val data: String,
    val keyType: KeyType,
    val keySize: Int,
    val contentType: ContentType,
    val outcome: Boolean
)
