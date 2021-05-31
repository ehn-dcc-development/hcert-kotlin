package ehn.techiop.hcert.kotlin.chain.impl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FileBasedCryptoServiceTest : StringSpec({

    "good" {
        val pemEncodedPrivateKey = "-----BEGIN PRIVATE KEY-----\n" +
                "ME0CAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEMzAxAgEBBCBTeBw5ZwlNAAdw+rFn\n" +
                "Ctkkn8KhecdSz3Ft4fSBVNOG3KAKBggqhkjOPQMBBw==\n" +
                "-----END PRIVATE KEY-----\n"
        val pemEncodedCertificate = "-----BEGIN CERTIFICATE-----\n" +
                "MIIBIzCByqADAgECAgRi5XwLMAoGCCqGSM49BAMCMBAxDjAMBgNVBAMMBUVDLU1l\n" +
                "MB4XDTIxMDQyMzEwMzc1NVoXDTIxMDUyMzEwMzc1NVowEDEOMAwGA1UEAwwFRUMt\n" +
                "TWUwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAAT4pyqh0AMFtrN/rLF4tKBB+Rhp\n" +
                "6ttuC6JTQ4c4fIy9f6H/Hjko8v6fYWkz3WrhKV7e0ScI4RLbT6nrv/F/6sJQoxIw\n" +
                "EDAOBgNVHQ8BAf8EBAMCBaAwCgYIKoZIzj0EAwIDSAAwRQIhAMQjFFnmgFx1scLH\n" +
                "6+iY9Vyu3EYkHEzNXUv7Zr/H6gJDAiAw7Sry/U7h/X+Hk1MncAqln7dpK2MDKABc\n" +
                "46ByFwZ+Bw==\n" +
                "-----END CERTIFICATE-----\n"
        val service = FileBasedCryptoService(pemEncodedPrivateKey, pemEncodedCertificate)
        service.exportCertificateAsPem() shouldBe pemEncodedCertificate
        service.exportPrivateKeyAsPem() shouldBe pemEncodedPrivateKey
    }
    //    withData(256, 384) { keySize ->
    //        val input = RandomEcKeyCryptoService(keySize)
    //        val privateKeyPem = input.exportPrivateKeyAsPem()
    //        val certificatePem = input.exportCertificateAsPem()
    //        val parsed = FileBasedCryptoService(privateKeyPem, certificatePem)
    //
    //        input.getCborSigningKey().oneKey.EncodeToBytes() shouldBe (parsed.getCborSigningKey().oneKey.EncodeToBytes())
    //        input.getCertificate().kid shouldBe (parsed.getCertificate().kid)
    //    }
    //    withData(2048, 3072) { keySize ->
    //        val input = RandomRsaKeyCryptoService(keySize)
    //        val privateKeyPem = input.exportPrivateKeyAsPem()
    //        val certificatePem = input.exportCertificateAsPem()
    //        val parsed = FileBasedCryptoService(privateKeyPem, certificatePem)
    //
    //        input.getCborSigningKey().oneKey.EncodeToBytes() shouldBe (parsed.getCborSigningKey().oneKey.EncodeToBytes())
    //        input.getCertificate().kid shouldBe (parsed.getCertificate().kid)
    //    }
})