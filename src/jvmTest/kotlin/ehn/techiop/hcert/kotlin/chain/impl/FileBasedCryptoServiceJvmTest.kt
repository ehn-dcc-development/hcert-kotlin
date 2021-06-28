package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.crypto.JvmPrivKey
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class FileBasedCryptoServiceJvmTest : DescribeSpec({

    withData(256, 384) { keySize ->
        val input = RandomEcKeyCryptoService(keySize)
        val privateKeyPem = input.exportPrivateKeyAsPem()
        val certificatePem = input.exportCertificateAsPem()
        val parsed = FileBasedCryptoService(privateKeyPem, certificatePem)

        (input.getCborSigningKey() as JvmPrivKey).toCoseRepresentation().EncodeToBytes()
            .shouldBe((parsed.getCborSigningKey() as JvmPrivKey).toCoseRepresentation().EncodeToBytes())
        input.getCertificate().kid shouldBe (parsed.getCertificate().kid)

        parsed.getCertificate().certificate.verify(parsed.getCertificate().certificate.publicKey)
    }

    withData(2048, 3072) { keySize ->
        val input = RandomRsaKeyCryptoService(keySize)
        val privateKeyPem = input.exportPrivateKeyAsPem()
        val certificatePem = input.exportCertificateAsPem()
        val parsed = FileBasedCryptoService(privateKeyPem, certificatePem)

        (input.getCborSigningKey() as JvmPrivKey).toCoseRepresentation().EncodeToBytes()
            .shouldBe((parsed.getCborSigningKey() as JvmPrivKey).toCoseRepresentation().EncodeToBytes())
        input.getCertificate().kid shouldBe (parsed.getCertificate().kid)

        parsed.getCertificate().certificate.verify(parsed.getCertificate().certificate.publicKey)
    }

})