package ehn.techiop.hcert.kotlin.chain.impl

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class FileBasedCryptoServiceJvmTest : DescribeSpec({

    withData(256, 384) { keySize ->
        val input = RandomEcKeyCryptoService(keySize)
        val privateKeyPem = input.exportPrivateKeyAsPem()
        val certificatePem = input.exportCertificateAsPem()
        val parsed = FileBasedCryptoService(privateKeyPem, certificatePem)

        input.getCborSigningKey().oneKey.EncodeToBytes() shouldBe (parsed.getCborSigningKey().oneKey.EncodeToBytes())
        input.getCertificate().kid shouldBe (parsed.getCertificate().kid)

        parsed.getCertificate().certificate.verify(parsed.getCertificate().certificate.publicKey)
    }

    withData(2048, 3072) { keySize ->
        val input = RandomRsaKeyCryptoService(keySize)
        val privateKeyPem = input.exportPrivateKeyAsPem()
        val certificatePem = input.exportCertificateAsPem()
        val parsed = FileBasedCryptoService(privateKeyPem, certificatePem)

        input.getCborSigningKey().oneKey.EncodeToBytes() shouldBe (parsed.getCborSigningKey().oneKey.EncodeToBytes())
        input.getCertificate().kid shouldBe (parsed.getCertificate().kid)

        parsed.getCertificate().certificate.verify(parsed.getCertificate().certificate.publicKey)
    }

    it("importEc256Key") {
        val pemEncodedPrivateKey = "-----BEGIN PRIVATE KEY-----\n" +
                "ME0CAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEMzAxAgEBBCDiN+UZ4xVGN/Tedqgh\n" +
                "ymZUo6BpFDlMAVPe27FMYvravKAKBggqhkjOPQMBBw==\n" +
                "-----END PRIVATE KEY-----\n"
        val pemEncodedCertificate = "-----BEGIN CERTIFICATE-----\n" +
                "MIIBRTCB7KADAgECAgQWULHhMAoGCCqGSM49BAMCMBAxDjAMBgNVBAMTBUVDLU1l\n" +
                "MB4XDTIxMDYxMDA2MzkyM1oXDTIxMDcxMDA2MzkyM1owEDEOMAwGA1UEAxMFRUMt\n" +
                "TWUwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAAR7Zu7mpxMTbAOJAiXY149BBk3V\n" +
                "OxADHL3l7qLvPfqsaoocS/qNpVfL0mN9H933cMBEgiVY2tJ5aQp189QuRBylozQw\n" +
                "MjAwBgNVHSUEKTAnBgsrBgEEAY43j2UBAQYLKwYBBAGON49lAQIGCysGAQQBjjeP\n" +
                "ZQEDMAoGCCqGSM49BAMCA0gAMEUCIQDAgt3gGnGOisGKSV/t/Qa1CKdba7Tf+qAd\n" +
                "LzoK7csDbAIgaRy4eyG3UuyMoEt9yE6b0mi575ksOGB3PDZZd0P0pHg=\n" +
                "-----END CERTIFICATE-----\n"
        val parsed = FileBasedCryptoService(pemEncodedPrivateKey, pemEncodedCertificate)
        parsed.exportCertificateAsPem() shouldBe pemEncodedCertificate
        parsed.exportPrivateKeyAsPem() shouldBe pemEncodedPrivateKey

        parsed.getCertificate().certificate.verify(parsed.getCertificate().certificate.publicKey)
    }

    it("importRsa2048Key") {
        val pemEncodedPrivateKey = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCnxHyjkMMwGN+9\n" +
                "BOOim0DQKMNH9aLpKtZWinuGu5tbu7axxDlYaLxQk7tzmJhkOK/S6SvlGhZxUc65\n" +
                "iN5JiN4rluWOxCSoIZI44N/6uNrgGYjKOGtINt2ifRk8LwpSeG1A0w3JVsWPnHva\n" +
                "Z69tXjEYP52D5F5sdu0VemK0NmVT2Pv3HQKJXmCa1FAaCOLLNmTLfZ/X1PtkPjaY\n" +
                "CmC5Ukdwks3L132Dav1vMGuuxVr+DTwnGVu4owjpGa8FQhwhX05Rqkse9dHQZliZ\n" +
                "LLVRcdbECFI4+45CeAgniE8rLfvSeoJFxqe3GZAkoXh2Li7e8uzI0Vgwd4yGIPEl\n" +
                "9PMesZhjAgMBAAECggEAab/CyN9RCZpEPcM9MZwnKI7pOQyumcb0wCweXPkYnqQe\n" +
                "LsN/Wij6utfESsMfG91XhtyPXKQrDVcBtZV+eF+bm2JMZMdiS9CNeOjWhhXLcYLm\n" +
                "5IDXrvdWkTvFklgca4uZYkkm4Yiekz2SHENLIudCjwRaZ26lzjzQiL9SREIdcNHG\n" +
                "N97p5F+ve2T3wJ2rSpEzkmgnSKTSVwwJ9tlTe5WDH5FbeqyyN2B/z8Be8OBnwJpW\n" +
                "2GfcXxeo9AmhASY3qyAuFxWPfAkBThEQkXBvovYP7P7Tvx5kOOcN1C8LgeFoS66C\n" +
                "TTlRqbX6IRaAMcz2tuAVucwgx+3Bk54kVA6Jx9BS8QKBgQD167IufHr3VQENCIlc\n" +
                "il5TTNTQq+m44ugiZoCS2qy3iGe7Hi256sLV4peDFevOOOEotGEbB66zUXoZBwT7\n" +
                "p9FxhxYCya7aCUfZ3xpCw2CXLAf2LZ2l0NLImXtc/5AfTv3k+8S5AgoevxUxh1fU\n" +
                "PfR4Ych5SlQeGkHxfmhYbdTKvQKBgQCupMZZB+7tHUs0dVLhqIR+fag9taQ5H6se\n" +
                "8jJMwTq4famvPYhebEG8F0LjqA+4dyzybPbplGLHH8GpyBsvz3VjlOcPJja8V2vX\n" +
                "tpueXy6w9+b4yvddA8a+teq9fV/zqLaxd+SQupcfQSFVOLLtsrzDJJQT28xkA6N1\n" +
                "L7rsB/exnwKBgAjWPmHjQTvwBwij3OPBaSBPK3qQIubs6HCMBGTCjOBPe4LC09vR\n" +
                "bszmxpEEUaPocVbYHIEvJnz1GZEaqngskJ8mvoi9HJZjLPzMQpJRmP/1qheTjg2u\n" +
                "UlS5BzDRUil92ivHTEtlol12ell8RpCu5UEKKn0JSbqHMQVviGpdD/91AoGBAIRT\n" +
                "k3p4GWAOVyJbVz5mSmXxXh3L2K/zv9wF1Xj03EJX7M88B2zMCA61bVhDyg2SNztE\n" +
                "tt2LMkEAXeQAHCFgxuh6NYzz/ns8nhjnYwhfxGgQnjCa0UzoBJoSCtgi/CYKCstz\n" +
                "NXs39jOXTONev6x9RqtxtsVJQJfzbdHoJXCcJOCvAoGBAJiF72Yn9f0MjTpMm2tn\n" +
                "z5nSe61rxhMAhPv9Z7OWQsR8BvnYbgr/zpHYrKO9pezXB1k4+9nE+uB4I47APLj2\n" +
                "izYcuOgTeLHwRYJKqJa50YTZzgT/R8iAVYtLjdLPKHThzxS7sBJi/taLOIlOK4uk\n" +
                "ZLzvdbAIm1T+bMylVIHAP0Ap\n" +
                "-----END PRIVATE KEY-----\n"
        val pemEncodedCertificate = "-----BEGIN CERTIFICATE-----\n" +
                "MIIC0DCCAbqgAwIBAgIEdoXlMzALBgkqhkiG9w0BAQswETEPMA0GA1UEAxMGUlNB\n" +
                "LU1lMB4XDTIxMDYxMDA2MzkyNFoXDTIxMDcxMDA2MzkyNFowETEPMA0GA1UEAxMG\n" +
                "UlNBLU1lMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp8R8o5DDMBjf\n" +
                "vQTjoptA0CjDR/Wi6SrWVop7hrubW7u2scQ5WGi8UJO7c5iYZDiv0ukr5RoWcVHO\n" +
                "uYjeSYjeK5bljsQkqCGSOODf+rja4BmIyjhrSDbdon0ZPC8KUnhtQNMNyVbFj5x7\n" +
                "2mevbV4xGD+dg+RebHbtFXpitDZlU9j79x0CiV5gmtRQGgjiyzZky32f19T7ZD42\n" +
                "mApguVJHcJLNy9d9g2r9bzBrrsVa/g08JxlbuKMI6RmvBUIcIV9OUapLHvXR0GZY\n" +
                "mSy1UXHWxAhSOPuOQngIJ4hPKy370nqCRcantxmQJKF4di4u3vLsyNFYMHeMhiDx\n" +
                "JfTzHrGYYwIDAQABozQwMjAwBgNVHSUEKTAnBgsrBgEEAY43j2UBAQYLKwYBBAGO\n" +
                "N49lAQIGCysGAQQBjjePZQEDMAsGCSqGSIb3DQEBCwOCAQEADsCO3BH0XRw1bhq3\n" +
                "33nVywHQV1Ih+/ePCIXITJw8+ATQFuXGifYAT29bdaK7R+MCZRlrOv0DgnykmlvC\n" +
                "8W6vNwwRfgNZY4L7gwqY6eFRenk/do2UVdXQqjZFhy79+QeL5GS4oG0rvCIchxqE\n" +
                "TfCypcVLNZElVuUFaZTJ2pW1coLZWL8pqIB6vwDHp7Ygq4HolsDt4jktfvnAzTjQ\n" +
                "EigbM9xRZq63OJRfceQrijH2JiIEfum/oqUBSWCsiTlZcYh/AyubtNQhTKbZ5MR6\n" +
                "Nk79bBHv0iYJYdIWxRJ9iZtFyUl+B7TxCoLeplahTBsNT3P6qqLgESLSeiFUtyL6\n" +
                "E1xwOg==\n" +
                "-----END CERTIFICATE-----\n"
        val parsed = FileBasedCryptoService(pemEncodedPrivateKey, pemEncodedCertificate)
        parsed.exportCertificateAsPem() shouldBe pemEncodedCertificate
        parsed.exportPrivateKeyAsPem() shouldBe pemEncodedPrivateKey

        parsed.getCertificate().certificate.verify(parsed.getCertificate().certificate.publicKey)
    }

})