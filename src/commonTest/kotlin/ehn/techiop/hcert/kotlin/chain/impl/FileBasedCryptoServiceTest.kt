package ehn.techiop.hcert.kotlin.chain.impl

import ehn.techiop.hcert.kotlin.chain.CryptoService
import ehn.techiop.hcert.kotlin.chain.VerificationResult
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.random.Random

class FileBasedCryptoServiceTest : DescribeSpec({

    it("importEc256Key") {
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
        shouldBeIgnoringNewlines(service.exportCertificateAsPem(), pemEncodedCertificate)
        shouldBeIgnoringNewlines(service.exportPrivateKeyAsPem(), pemEncodedPrivateKey)

        assertEncodeDecode(service)
    }

    it("importEc384Key") {
        val pemEncodedPrivateKey = "-----BEGIN PRIVATE KEY-----\n" +
                "MFcCAQAwEAYHKoZIzj0CAQYFK4EEACIEQDA+AgEBBDBNXQ3opUJLPgWLnG2pSC7z\n" +
                "WxWNH9eVPy+2C6a//3W06l3kDBqHYv/zyFN6P9/QsDmgBwYFK4EEACI=\n" +
                "-----END PRIVATE KEY-----\n"
        val pemEncodedCertificate = "-----BEGIN CERTIFICATE-----\n" +
                "MIIBkzCCARmgAwIBAgIEd4CHhjAKBggqhkjOPQQDAjAQMQ4wDAYDVQQDDAVFQy1N\n" +
                "ZTAeFw0yMTA2MjgwODUwMDFaFw0yMTA3MjgwODUwMDFaMBAxDjAMBgNVBAMMBUVD\n" +
                "LU1lMHYwEAYHKoZIzj0CAQYFK4EEACIDYgAED/IPvION844AAlXSjVbksLMVws9B\n" +
                "6UlcGretod8oq3LidqHw0k/O6D3hBV/k6miYUW7bDty5iAm+6LEUPFjCvGml9rnZ\n" +
                "5ysJxDCcFGXV1lmjaoZuPDNsFb5Bl/5VXQw7o0QwQjAOBgNVHQ8BAf8EBAMCBaAw\n" +
                "MAYDVR0lBCkwJwYLKwYBBAGON49lAQEGCysGAQQBjjePZQECBgsrBgEEAY43j2UB\n" +
                "AzAKBggqhkjOPQQDAgNoADBlAjEAykgQfGCjanRSKa6oE1cUgkz4wBzJvHVfeaFK\n" +
                "r42v8iG/HQrUH5wEXyaFlHzzTSVjAjB+SDGyNjCipEs2pgxiTeCDQFnwqX9LoJHP\n" +
                "bmnpoCd7Mdyiz0TkXkNAkvNg9qFgXPo=\n" +
                "-----END CERTIFICATE-----\n"
        val service = FileBasedCryptoService(pemEncodedPrivateKey, pemEncodedCertificate)
        shouldBeIgnoringNewlines(service.exportCertificateAsPem(), pemEncodedCertificate)
        shouldBeIgnoringNewlines(service.exportPrivateKeyAsPem(), pemEncodedPrivateKey)

        assertEncodeDecode(service)
    }

    it("importRsa2048Key") {
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
        shouldBeIgnoringNewlines(service.exportCertificateAsPem(), pemEncodedCertificate)
        shouldBeIgnoringNewlines(service.exportPrivateKeyAsPem(), pemEncodedPrivateKey)

        assertEncodeDecode(service)
    }

    it("importRsa3072Key") {
        val pemEncodedPrivateKey = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIG/AIBADANBgkqhkiG9w0BAQEFAASCBuYwggbiAgEAAoIBgQCSO4f64U09WEFO" +
                "aC2y843UQKKZF0xavVuqtUfwGsWT+1RVauyTtWFth7SRg48++tsPUyXFHqdHn/su" +
                "Lrpr2X6u94jJWjyuP42Ix9vmTPxWCPelfiSJb4ishspYugUVG6tDO+UhGuuRPlA8" +
                "7rkcLenp6SlY3fZMI40rlQ14nPIHnIqjxW7+vx13bmL/xwY/qKYrXB7vs0JVznHN" +
                "uZnxCa83m+I5YmRznz71wefOWHnRDi2N3NmaWinRqXhdibz8DjQsN4AAxj1C4ULw" +
                "uiSZszQNByID6uZsbW7rLwdHMIZXW/zrCjRMvhxCmydpQwEr8/c/P4+l9CU9Y4qp" +
                "/Sn38kq0nTusB+TiT5hPm3+TMgp/fLYrh4uJekrmMriRsI7F62wdvP8E5NIMptyA" +
                "/HkmKwJ2ynZYt+v7ll5H3wBF1YJx8/v++V5jL2224+bv6xnM3LnFYrk6m6Kgk7C/" +
                "w3Sk+Pt4VTzgxNCCAp7zmaHsBFb6EO2Yq4HIQnWmOvmsBo3Z1LMCAwEAAQKCAYAk" +
                "EICJ1DT7cx+wGatjngOVnFc9kGxpWJgZ8tmKEuA1jd+PrW4pQ6uCmtxiouKaMr4B" +
                "+oyvH887r+3/xtB9NseymSPoHNQoWU4rtLa8BrKY+V8yNnkGWDaJ7jhLR51nRkqn" +
                "q0boMj50tLyPOoT2uTAeWz2ySBOtVXtEi18mJvbd/7KFj3S/aRz4ToYl9MzKuGo6" +
                "+V472ab7iI2yohqgQrYu7cgORQDnLxgU/jyQPGvWrjt36EbWdXQo3DXuQ8ETHOFL" +
                "dZxGCrcq0ND4Q9HCReq1J9a7snQZhc8p9xiuGrDCUJtfMVNzG3xO7PP+J77x+otn" +
                "kr4g1paHCQNxMq/hvIfC2GT/qqMbXoBs85dej5zEi0+1O/Q5f/ae3nzSHKLwD+Wt" +
                "X2oGrfXAY93DC0mtwDwpmgdIbmKy08njsDCzOeC9GJq7XyJeWfIrOCvR3xFpwgCC" +
                "ytd3v/Eexsoc9G9UXTnUeAcMbNlFEs5QyGC/g8LY0Uhhert3mcon7G0SpcplJDEC" +
                "gcEAz6gE0EweMrwyZsQWHJS6hsXmSg9qGtRhOTOJte1u3Z0neBvOwH9KYGDxlrDB" +
                "5pZjU8L+Vs3rhQFYsFDstdULpMZXKI0/FZ2Sx9ZNbIIppIY6FEBRhZCjiAX7vmMi" +
                "QOj0/YRlh+LtvZWk/151XCrRtwg8W2H4AQNq9GV1zZ1A3zVnYldpepfgv045smha" +
                "y6PqUO6nypTig9pV9a/WQiWsdhY0k7ITbDiQXOHm0CCQaaYOwFw+/LKd0m1oZ9+8" +
                "EzWpAoHBALRGwR9bCuhr9ja7gDFiP6IPDuj1YpDDiabsQmQdtvRtAB8WFADuASfR" +
                "eHhHgMSjGnef5HwKYBZgmpIBwDim7H0z6ltpwFxjyC4bgu0AwtavZZt1AkWbweZ2" +
                "7kOfc5KR86UfKAjc4sxztwZQB5sGUkQmoSe9tb7SrIECdbbiVQH1Pg3VY4y1AJYr" +
                "7/O0VRgyfIr4aWVkY/u3vaiBgLEXWS6YwAm6mfTL0yMRm1acgwjqcpdy1doIynLA" +
                "GsrAJxV4+wKBwB4PVXsNucVgZu9nbUf/46yE01xfNN3jZpuQMVwrncPo7wC9DcXh" +
                "51NiVTaaKceGd6R1croAHxrm4f7MCCgeSgw1RgKxx7MKV2gkRprxkLOnVpr94I80" +
                "K+gR7apW0WuSDXzxgH0WRZIPKo5pDxYjgK49O/eCjBMheoccdNwp6m0lXuzmeHdt" +
                "qvQmj8Waw6H8/XtvwMxblxq9LXpeVObIa1nAxyWmPpeI7KT1cqMoQ593kWUZNWkB" +
                "NkVy3w1CXwhs2QKBwDPys5HahVEmUyPAchdI7XdAiZZdp5J5lj/wks0QtvjGNCPB" +
                "E/CUpOaZePKIADYpneXcwFVyE/A+1CvAr2hjyX/mpuDCn3jAFB4dNWQgNQTyNABQ" +
                "/l+am4xStyPrXb1dq2zohnvPUteBfLsn8pkcgDY9P3EZPLcJNEZUtCEIJWDUD5ai" +
                "5/XS7qFXdeSvEVpQqgzNumlKPXl6n/9fmfrt9Hf18libWePtnCDd4PpbTXMTRAQa" +
                "Rj93Cwd4DgBg0KJiYwKBwF4oDgBvP1JLo2OFqj2Gruqab6B1Im8C4hGIq+VxiFni" +
                "iJ2KEYQfykjDkMg1TnwlMCjCZgG1MZCr7oBvf5Fi/Gp4F87uIqjLGSYyGIuT4d5f" +
                "naQZupSUfIIT3jpg4wnkG+kAXBovW6sHB1ZEBmILO6ucV+tybiFa77OYcuJJaXCr" +
                "0YVQs0iIb3bLREPXz1/6r1PbfYuRv6HH00vocmA9/Y9n+5Q1bRZSOI/wX5kZcfPN" +
                "K8DuMJ5PaJZfhWjsNmieqg==\n" +
                "-----END PRIVATE KEY-----"
        val pemEncodedCertificate = "-----BEGIN CERTIFICATE-----\n" +
                "MIID5TCCAk2gAwIBAgIFAOw3iSMwDQYJKoZIhvcNAQELBQAwETEPMA0GA1UEAwwG\n" +
                "UlNBLU1lMB4XDTIxMDYyODA4NTAwMloXDTIxMDcyODA4NTAwMlowETEPMA0GA1UE\n" +
                "AwwGUlNBLU1lMIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAkjuH+uFN\n" +
                "PVhBTmgtsvON1ECimRdMWr1bqrVH8BrFk/tUVWrsk7VhbYe0kYOPPvrbD1MlxR6n\n" +
                "R5/7Li66a9l+rveIyVo8rj+NiMfb5kz8Vgj3pX4kiW+IrIbKWLoFFRurQzvlIRrr\n" +
                "kT5QPO65HC3p6ekpWN32TCONK5UNeJzyB5yKo8Vu/r8dd25i/8cGP6imK1we77NC\n" +
                "Vc5xzbmZ8QmvN5viOWJkc58+9cHnzlh50Q4tjdzZmlop0al4XYm8/A40LDeAAMY9\n" +
                "QuFC8LokmbM0DQciA+rmbG1u6y8HRzCGV1v86wo0TL4cQpsnaUMBK/P3Pz+PpfQl\n" +
                "PWOKqf0p9/JKtJ07rAfk4k+YT5t/kzIKf3y2K4eLiXpK5jK4kbCOxetsHbz/BOTS\n" +
                "DKbcgPx5JisCdsp2WLfr+5ZeR98ARdWCcfP7/vleYy9ttuPm7+sZzNy5xWK5Opui\n" +
                "oJOwv8N0pPj7eFU84MTQggKe85mh7ARW+hDtmKuByEJ1pjr5rAaN2dSzAgMBAAGj\n" +
                "RDBCMA4GA1UdDwEB/wQEAwIFoDAwBgNVHSUEKTAnBgsrBgEEAY43j2UBAQYLKwYB\n" +
                "BAGON49lAQIGCysGAQQBjjePZQEDMA0GCSqGSIb3DQEBCwUAA4IBgQBTPuiUgKOb\n" +
                "iY3Di8BCA60igkQzI0sJZfOx0x19l5un29F6Dl/koAmbTlZCwCWuIZSJh2/4mATc\n" +
                "c+GJbaQdbA+WycWGNA2Y/ALKoCzCuoCFY9NSXwkIJyL90xymrMGmn0YfNGbRY+L/\n" +
                "GLIN7vSyCkUGTZFZyWdpJaWPoEL58lmaA4K2WgbRxMop/QcCiXVfVJBp11c2t3rE\n" +
                "wKcVwp0j0m+BubcNWyT5y8D952VF8pu55XyYlE3I+veamH6HFGQAnwYBJdLsvsjd\n" +
                "gkd7urvstuHfwiVFxDqS3xYgPN8IbkLpkacCZcrLKpkX0kPgwdp/xtASzdefohZj\n" +
                "eLcxobdcXMmAl++ivJaMshksvkAYll1xe43CJgTKQ6HPbuZhugoW7Rcn5aezBm/y\n" +
                "Ol3QQTu73pFLmaZ9rydAP1I3CIUWhP9VN1wKJtSp9HtKJufrF9NejcXodIO/5XwL\n" +
                "yKTc2RyWZmS9usbAVjp54m/pNkfvKfbgfbFb4ztQRuMlOTE6ul6A7/w=\n" +
                "-----END CERTIFICATE-----\n"
        val service = FileBasedCryptoService(pemEncodedPrivateKey, pemEncodedCertificate)
        shouldBeIgnoringNewlines(service.exportCertificateAsPem(), pemEncodedCertificate)
        shouldBeIgnoringNewlines(service.exportPrivateKeyAsPem(), pemEncodedPrivateKey)

        assertEncodeDecode(service)
    }

})

private fun shouldBeIgnoringNewlines(actualValue: String, expectedValue: String) {
    actualValue.replace("\n", "") shouldBe expectedValue.replace("\n", "")
}

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

