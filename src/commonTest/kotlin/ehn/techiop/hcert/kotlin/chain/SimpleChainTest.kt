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

class SimpleChainTest : DescribeSpec({

    timeout = Duration.seconds(5).inWholeMilliseconds

    val listOfInput = listOf(
        SCInput(testRat, TEST, EC, 256),
        SCInput(testNaa, TEST, EC, 256),
        SCInput(vaccination, VACCINATION, EC, 256),
        SCInput(recovery, RECOVERY, EC, 256),

        SCInput(testRat, TEST, EC, 384),
        SCInput(testNaa, TEST, EC, 384),
        SCInput(vaccination, VACCINATION, EC, 384),
        SCInput(recovery, RECOVERY, EC, 384),

        SCInput(testRat, TEST, RSA, 2048),
        SCInput(testNaa, TEST, RSA, 2048),
        SCInput(vaccination, VACCINATION, RSA, 2048),
        SCInput(recovery, RECOVERY, RSA, 2048),

        SCInput(testRat, TEST, RSA, 3072),
        SCInput(testNaa, TEST, RSA, 3072),
        SCInput(vaccination, VACCINATION, RSA, 3072),
        SCInput(recovery, RECOVERY, RSA, 3072),
    )

    withData(nameFn = { "${it.dataType} ${it.keyType}-${it.keySize}" }, listOfInput) { input ->
        val service = CryptoServiceHolder.getRandomCryptoService(input.keyType, input.keySize, null)
        val dataInput = Json.decodeFromString<GreenCertificate>(input.data)
        val encodingChain = DefaultChain.buildCreationChain(service)
        val certificateRepository = PrefilledCertificateRepository(service.getCertificate())
        val decodingChain = DefaultChain.buildVerificationChain(certificateRepository)

        val output = encodingChain.encode(dataInput)

        val result = decodingChain.decode(output.step5Prefixed)
        (result.verificationResult.error == null) shouldBe true
        result.chainDecodeResult.eudgc shouldBe dataInput
    }

})

private data class SCInput(
    val data: String,
    val dataType: ContentType,
    val keyType: KeyType,
    val keySize: Int,
)


