package ehn.techiop.hcert.kotlin.chain.impl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class FileBasedCryptoServiceTest : StringSpec({

    "goodEcKey" {
        val pemEncodedPrivateKey = "-----BEGIN PRIVATE KEY-----\n" +
                "ME0CAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEMzAxAgEBBCCOpgFH1YNIU9vzJWH0\n" +
                "DkR7lDM2LZWvzlfsTi3t5yjXA6AKBggqhkjOPQMBBw==\n" +
                "-----END PRIVATE KEY-----\n"
        val pemEncodedCertificate = "-----BEGIN CERTIFICATE-----\n" +
                "MIIBWTCCAQCgAwIBAgIFAI2jlhowCgYIKoZIzj0EAwIwEDEOMAwGA1UEAwwFRUMt\n" +
                "TWUwHhcNMjEwNTMxMTUzNzExWhcNMjEwNjMwMTUzNzExWjAQMQ4wDAYDVQQDDAVF\n" +
                "Qy1NZTBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABH1hRvgNXdbX5RVHXyuKIjn0\n" +
                "BNYRsK3cySMWV+m1BZ9nlhDqMXTFibjBFOVLDYKoL3bZNEfCKw9sn6cdhE4nuu+j\n" +
                "RzBFMA4GA1UdDwEB/wQEAwIFoDAzBgNVHSUELDAqBgwrBgEEAQCON49lAQEGDCsG\n" +
                "AQQBAI43j2UBAgYMKwYBBAEAjjePZQEDMAoGCCqGSM49BAMCA0cAMEQCIBrfOypj\n" +
                "4mKnqQJfmeRnASBqMWIq7M8vGE0U0wRHv3o7AiBoD69F7gzdMgZ31uzTFpxdkKoS\n" +
                "zT4F0Pw48h7dfUeW6A==\n" +
                "-----END CERTIFICATE-----\n"
        val service = FileBasedCryptoService(pemEncodedPrivateKey, pemEncodedCertificate)
        service.exportCertificateAsPem() shouldBe pemEncodedCertificate
        service.exportPrivateKeyAsPem() shouldBe pemEncodedPrivateKey

        // TODO Sign something
        //  DefaultCoseService(service).encode("foo".encodeToByteArray())
    }

    "newEcKey" {
        val service = RandomEcKeyCryptoService()
        service.exportPrivateKeyAsPem() shouldNotBe null
        service.exportCertificateAsPem() shouldNotBe null
    }

    "goodRsaKey" {
        val pemEncodedPrivateKey = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCEoba3OQUobgqB\n" +
                "4bTBnlYgqBP2kxjfmLfyaScYBYDSrEyFE7hIZr1WipyNg5QT2g7KJmdY5EbF8s0M\n" +
                "l/2Ig0Xwi4RfMyQ+WOuXGFQwHu9dl1YtfNHN8eym4EtmNThbAGZA+pLPMM4+AQb3\n" +
                "43/q91ADMG+SRgO6Ylr14SV12sYlk+YZDYeEckEfKoCsorTfhl+liBaB1RlOdNwm\n" +
                "YbwEux9oN2ZlTSoPWYGbsD/vxtjJGtZ71eekTumSznOlG47NU2A3mT9rv+Vs8JbG\n" +
                "jS27ewVxzqIBGqugwvRryi15fOxCvvN3I/Edso35GyiLkfQcQUfAjcwXaeZQ+Doo\n" +
                "//9h6sSxAgMBAAECggEAGqnD6Kue3NTaaeftBauGFwSTFtPVgUXbXPFEubCJiFC5\n" +
                "BVvEhVGaKKau+EgGYcNJi9wSlK03iR3ZmPmJL4NscQIrN1Q5qgsIOZTbf90IM+Fz\n" +
                "oqtgJi3HbHjUz5RNYwX+iHuXPe3K2G4ub3EdyyeyvyFinJ5Uq4iQTrPXawzzHqSC\n" +
                "RbsVD0JFbgnjXuvOTQtNIOTbWo2clUgizsBosmsM/odVM0L2Dj75e0rLgF+A9Nd1\n" +
                "Ii+SDXgyyJMDB8/FAHn4KmfoeEx5td1dDIPZD3ntTqaS+wx9NJGp2PTw4y6O+DHa\n" +
                "3JjvfpqA/NDzbmysKK7DD+q8R4nsfBLgeuCme05nfQKBgQD7N/XVkexgEtEeAxrS\n" +
                "oIIOWCbFvB9VZl9ybi4Cxjnj738B75VKHlZ5OzYmlNMrEvDLprEji86fi5UidWvg\n" +
                "KBkUKY5vR55dGdkAJ0XXNxcejPnw4po46hy71D0d1elZgRhcSNasb+aj8mXiG47L\n" +
                "ZNX8KN5yVymsDsBH5gcVah1srwKBgQCHJ/MXd2JJvCv3wgyIeqNa1sZynNkMgHwB\n" +
                "auIHLyjQfnwfn+Y1/wVUshSnuJaoPq0zRl9OYi14bukJeIk2hRIGLLPX7cjF0PaJ\n" +
                "0dbmiBAZ8lZmM5grCEfHkh1RY+Ssmqga5J/Y3d5DxUQ94Xdm3lcmi1myMcLmGquC\n" +
                "dj9c4dj8nwKBgAblX1sweUOd4J2pSigz/b31D4NoCZgnikEy4xJybI5kOaFM+VUi\n" +
                "hg8n3/GpLi7Fg1Sjy8MFCHP6uepLPN3XW/Dgvycw2RkHJ5zIdzNUMM9G4WmKXt9n\n" +
                "FcjWJ6NVBuXNFGUcHsB3BebENaXCSeYta25TlN+gouU1NnQCzXj6A7rDAoGAT8Vz\n" +
                "hCExgOWwab49mXwQ2He1j0YmEWvwRQHpwGXESDKvXhcJUEthwRiOemPHgCvmHEJn\n" +
                "1CK8Rb3oi296RRSLi9tsloDBJIhuSu/wUAZ0wmu3NQE0yglMHG2QIk68VGe/2oeg\n" +
                "FOb23bcbzQ47ZBrNA3HyEeuu5hNNsWXLhi3C3W8CgYEAhZ4nRYuLWbOSTiS6k5dQ\n" +
                "fqXeOXnNAuEbYGO+rOSmvPGVCRy/+ExSSlIUtQSBUQp43lR2ByP/sepSK/uPQnH7\n" +
                "kIJo5qJtkOhL6ynRRDGJK6rKnNkjtXMP1cSy3aFGN6tCZ1yylA7RNDh20XuSIf++\n" +
                "+Q9tSvimM4TJ7gCSW37uIf8=\n" +
                "-----END PRIVATE KEY-----\n"
        val pemEncodedCertificate = "-----BEGIN CERTIFICATE-----\n" +
                "MIIC5zCCAc+gAwIBAgIEBgcp1zANBgkqhkiG9w0BAQsFADARMQ8wDQYDVQQDDAZS\n" +
                "U0EtTWUwHhcNMjEwNTMxMTUzNzExWhcNMjEwNjMwMTUzNzExWjARMQ8wDQYDVQQD\n" +
                "DAZSU0EtTWUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCEoba3OQUo\n" +
                "bgqB4bTBnlYgqBP2kxjfmLfyaScYBYDSrEyFE7hIZr1WipyNg5QT2g7KJmdY5EbF\n" +
                "8s0Ml/2Ig0Xwi4RfMyQ+WOuXGFQwHu9dl1YtfNHN8eym4EtmNThbAGZA+pLPMM4+\n" +
                "AQb343/q91ADMG+SRgO6Ylr14SV12sYlk+YZDYeEckEfKoCsorTfhl+liBaB1RlO\n" +
                "dNwmYbwEux9oN2ZlTSoPWYGbsD/vxtjJGtZ71eekTumSznOlG47NU2A3mT9rv+Vs\n" +
                "8JbGjS27ewVxzqIBGqugwvRryi15fOxCvvN3I/Edso35GyiLkfQcQUfAjcwXaeZQ\n" +
                "+Doo//9h6sSxAgMBAAGjRzBFMA4GA1UdDwEB/wQEAwIFoDAzBgNVHSUELDAqBgwr\n" +
                "BgEEAQCON49lAQEGDCsGAQQBAI43j2UBAgYMKwYBBAEAjjePZQEDMA0GCSqGSIb3\n" +
                "DQEBCwUAA4IBAQBWXGbcDo/SlbYhzQKZvWxhZc6/oyE+pQubZMe0BkQD2Hr52bTZ\n" +
                "5Yep4CcfXG8AKSzPs2RPG5DnNWhSDTp8MKAubThbFtbTY0kMWEsmt3TwQb+uKbf+\n" +
                "DKoi0ktyW2rAlIuuhzaMLmYs5G+gXHQISDULTRie0zDnyIRaZ6vxvpXhAWuCiwzu\n" +
                "4IhaDGKEI11mLE8JjhiCQNywi0VH9zKLIYuHeuI0RRf6PcxoHJNkp96liV7zXRx9\n" +
                "7ZHTuMdgO8eAeDsGvEotKjSxUOoQhSGS6gnj3dc7oSjSC7i1uSxW1Pa53P1xm0rJ\n" +
                "TQ0V07hdsexsYfHgEllbNCRcaeu5eVKwQvdN\n" +
                "-----END CERTIFICATE-----\n"
        val service = FileBasedCryptoService(pemEncodedPrivateKey, pemEncodedCertificate)
        service.exportCertificateAsPem() shouldBe pemEncodedCertificate
        service.exportPrivateKeyAsPem() shouldBe pemEncodedPrivateKey

        // TODO Sign something
        //  DefaultCoseService(service).encode("foo".encodeToByteArray())
    }

})