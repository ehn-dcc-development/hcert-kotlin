package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.impl.PrefilledCertificateRepository
import ehn.techiop.hcert.kotlin.chain.impl.RandomEcKeyCryptoService
import ehn.techiop.hcert.kotlin.chain.impl.RandomRsaKeyCryptoService
import ehn.techiop.hcert.kotlin.crypto.KeyType
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.time.Duration

class SimpleChainTest : DescribeSpec({

    timeout = Duration.seconds(5).inWholeMilliseconds

    val listOfInput = listOf(
        SimpleChainTestInput(SampleData.testRat, KeyType.EC, 256, true),
        SimpleChainTestInput(SampleData.testNaa, KeyType.EC, 256, true),
        SimpleChainTestInput(SampleData.vaccination, KeyType.EC, 256, true),
        SimpleChainTestInput(SampleData.recovery, KeyType.EC, 256, true),

        SimpleChainTestInput(SampleData.testRat, KeyType.EC, 384, true),
        SimpleChainTestInput(SampleData.testNaa, KeyType.EC, 384, true),
        SimpleChainTestInput(SampleData.vaccination, KeyType.EC, 384, true),
        SimpleChainTestInput(SampleData.recovery, KeyType.EC, 384, true),

        SimpleChainTestInput(SampleData.testRat, KeyType.RSA, 2048, true),
        SimpleChainTestInput(SampleData.testNaa, KeyType.RSA, 2048, true),
        SimpleChainTestInput(SampleData.vaccination, KeyType.RSA, 2048, true),
        SimpleChainTestInput(SampleData.recovery, KeyType.RSA, 2048, true),

        SimpleChainTestInput(SampleData.testRat, KeyType.RSA, 3072, true),
        SimpleChainTestInput(SampleData.testNaa, KeyType.RSA, 3072, true),
        SimpleChainTestInput(SampleData.vaccination, KeyType.RSA, 3072, true),
        SimpleChainTestInput(SampleData.recovery, KeyType.RSA, 3072, true),
    )

    withData(nameFn = { "${it.keyType}${it.keySize}" }, listOfInput) { input ->
        val service = if (input.keyType == KeyType.EC)
            RandomEcKeyCryptoService(input.keySize)
        else
            RandomRsaKeyCryptoService(input.keySize)
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

private data class SimpleChainTestInput(val data: String, val keyType: KeyType, val keySize: Int, val outcome: Boolean)


