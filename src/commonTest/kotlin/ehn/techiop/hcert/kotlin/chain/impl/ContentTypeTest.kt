package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.trust.ContentType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize

class ContentTypeTest : StringSpec({

    "oldOidValues" {
        val pemEncodedPrivateKey = "-----BEGIN PRIVATE KEY-----\n" +
                "ME0CAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEMzAxAgEBBCCOpgFH1YNIU9vzJWH0\n" +
                "DkR7lDM2LZWvzlfsTi3t5yjXA6AKBggqhkjOPQMBBw==\n" +
                "-----END PRIVATE KEY-----"
        val pemEncodedCertificate = "-----BEGIN CERTIFICATE-----\n" +
                "MIIBWTCCAQCgAwIBAgIFAI2jlhowCgYIKoZIzj0EAwIwEDEOMAwGA1UEAwwFRUMt\n" +
                "TWUwHhcNMjEwNTMxMTUzNzExWhcNMjEwNjMwMTUzNzExWjAQMQ4wDAYDVQQDDAVF\n" +
                "Qy1NZTBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABH1hRvgNXdbX5RVHXyuKIjn0\n" +
                "BNYRsK3cySMWV+m1BZ9nlhDqMXTFibjBFOVLDYKoL3bZNEfCKw9sn6cdhE4nuu+j\n" +
                "RzBFMA4GA1UdDwEB/wQEAwIFoDAzBgNVHSUELDAqBgwrBgEEAQCON49lAQEGDCsG\n" +
                "AQQBAI43j2UBAgYMKwYBBAEAjjePZQEDMAoGCCqGSM49BAMCA0cAMEQCIBrfOypj\n" +
                "4mKnqQJfmeRnASBqMWIq7M8vGE0U0wRHv3o7AiBoD69F7gzdMgZ31uzTFpxdkKoS\n" +
                "zT4F0Pw48h7dfUeW6A==\n" +
                "-----END CERTIFICATE-----"
        val service = FileBasedCryptoService(pemEncodedPrivateKey, pemEncodedCertificate)

        val parsedContentTypes = service.getCertificate().validContentTypes
        parsedContentTypes shouldHaveSize 3
        parsedContentTypes shouldContain ContentType.TEST
        parsedContentTypes shouldContain ContentType.VACCINATION
        parsedContentTypes shouldContain ContentType.RECOVERY
    }

    "oldOidTest" {
        val pemEncodedPrivateKey = "-----BEGIN PRIVATE KEY-----\n" +
                "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCC6Lx3GOaa5oYlBdZxy\n" +
                "CRLus8Rq9+OUaQxHholmQ0gpXQ==\n" +
                "-----END PRIVATE KEY-----"
        val pemEncodedCertificate = "-----BEGIN CERTIFICATE-----\n" +
                "MIIBPTCB5KADAgECAgUA0x7KBjAKBggqhkjOPQQDAjAQMQ4wDAYDVQQDDAVFQy1N\n" +
                "ZTAeFw0yMTA2MDkxMDI2MjVaFw0yMTA3MDkxMDI2MjVaMBAxDjAMBgNVBAMMBUVD\n" +
                "LU1lMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEVBxeCZfO41IiDCdC9qjmTlbw\n" +
                "0MSI+ft108s0zlSFar5hJFIWDvgdfHdiG1qNKoDN+O6BwEmnF6yLOJn8T3NxxaMr\n" +
                "MCkwDgYDVR0PAQH/BAQDAgWgMBcGA1UdJQQQMA4GDCsGAQQBAI43j2UBATAKBggq\n" +
                "hkjOPQQDAgNIADBFAiEA+Bip1Ek/7mLA5MGKsMXaxgL2DKla0PqfnqJ0WwE+NgEC\n" +
                "IFex2xg+sEFGAcqyVLhp/3DUZTTeKSSPndfyJxJ11YgQ\n" +
                "-----END CERTIFICATE-----"
        val service = FileBasedCryptoService(pemEncodedPrivateKey, pemEncodedCertificate)

        val parsedContentTypes = service.getCertificate().validContentTypes
        parsedContentTypes shouldHaveSize 1
        parsedContentTypes shouldContain ContentType.TEST
    }

    "newOidValues" {
        val pemEncodedPrivateKey = "-----BEGIN PRIVATE KEY-----\n" +
                "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCASjLkXjBUmLG2Aq/EG\n" +
                "xWeAdM45pkTdbxUoE3XX4uE5vw==\n" +
                "-----END PRIVATE KEY-----"
        val pemEncodedCertificate = "-----BEGIN CERTIFICATE-----\n" +
                "MIIBVjCB/aADAgECAgUAnCjAOTAKBggqhkjOPQQDAjAQMQ4wDAYDVQQDDAVFQy1N\n" +
                "ZTAeFw0yMTA2MDkxMDIzMzJaFw0yMTA3MDkxMDIzMzJaMBAxDjAMBgNVBAMMBUVD\n" +
                "LU1lMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEu7GcJbt+OzScKI6K7lPNwz3l\n" +
                "8szcEz9N3HKXjqmkluC6DKdvs+0tava2eiZlI9W/gOoOWtNeTXef2fwP3+UoPqNE\n" +
                "MEIwDgYDVR0PAQH/BAQDAgWgMDAGA1UdJQQpMCcGCysGAQQBjjePZQEBBgsrBgEE\n" +
                "AY43j2UBAgYLKwYBBAGON49lAQMwCgYIKoZIzj0EAwIDSAAwRQIhAOUt7ruuVYRD\n" +
                "oWpHS9piDjRgIXU4EbwJ0q/gY7v5YZrOAiABN83fydWUujvu57D1FbdTmo9m+psd\n" +
                "Yi1eg0MpZHwZiw==\n" +
                "-----END CERTIFICATE-----"
        val service = FileBasedCryptoService(pemEncodedPrivateKey, pemEncodedCertificate)

        val parsedContentTypes = service.getCertificate().validContentTypes
        parsedContentTypes shouldHaveSize 3
        parsedContentTypes shouldContain ContentType.TEST
        parsedContentTypes shouldContain ContentType.VACCINATION
        parsedContentTypes shouldContain ContentType.RECOVERY
    }

    "newOidTest" {
        val pemEncodedPrivateKey = "-----BEGIN PRIVATE KEY-----\n" +
                "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCCgMzdTABqUu9zl8C/v\n" +
                "e4eupDjc/K7ow2JPYJXAo2bFiQ==\n" +
                "-----END PRIVATE KEY-----"
        val pemEncodedCertificate = "-----BEGIN CERTIFICATE-----\n" +
                "MIIBPDCB46ADAgECAgUAn+JcKDAKBggqhkjOPQQDAjAQMQ4wDAYDVQQDDAVFQy1N\n" +
                "ZTAeFw0yMTA2MDkxMDI0NTlaFw0yMTA3MDkxMDI0NTlaMBAxDjAMBgNVBAMMBUVD\n" +
                "LU1lMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEDNbIn+WwiXyqiXldMS1bFoQB\n" +
                "u458zOEBSo0GU5bPODsyucMgL2j7/rF8xrbPjUQD4Y7hXj4mzqr1RdjtD5AibqMq\n" +
                "MCgwDgYDVR0PAQH/BAQDAgWgMBYGA1UdJQQPMA0GCysGAQQBjjePZQEBMAoGCCqG\n" +
                "SM49BAMCA0gAMEUCIQCm5QlcbDEhcSDMh+Hdm3CqGRETjl24Yyh0KLT6KL4SOQIg\n" +
                "e53yPbmJAFFhNSl6QNUqoDM4TKmQmcEyHz8wHq6XMOo=\n" +
                "-----END CERTIFICATE-----"
        val service = FileBasedCryptoService(pemEncodedPrivateKey, pemEncodedCertificate)

        val parsedContentTypes = service.getCertificate().validContentTypes
        parsedContentTypes shouldHaveSize 1
        parsedContentTypes shouldContain ContentType.TEST
    }
})