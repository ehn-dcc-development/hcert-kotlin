package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RandomCryptoServiceTest : DescribeSpec({

    //RSA key generation can take ages in JS
    timeout = 50.seconds.inWholeMilliseconds

    withData(nameFn = { "EC$it" }, 256, 384) { keySize ->
        val service = RandomEcKeyCryptoService(keySize)

        assertEncodeDecode(service)
    }

    withData(nameFn = { "RSA$it" }, 2048, 3072) { keySize ->
        val service = RandomRsaKeyCryptoService(keySize)

        assertEncodeDecode(service)
    }

})


private fun assertEncodeDecode(service: CryptoService) {
    service.exportPrivateKeyAsPem() shouldNotBe null
    service.exportCertificateAsPem() shouldNotBe null

    val plaintext = Random.nextBytes(32)
    val encoded = DefaultCoseService(service).encode(plaintext)
    encoded shouldNotBe null

    val verificationResult = VerificationResult()
    val repo = PrefilledCertificateRepository(service.exportCertificateAsPem())
    val decoded = DefaultCoseService(repo).decode(encoded, verificationResult)
    decoded shouldBe plaintext
}

