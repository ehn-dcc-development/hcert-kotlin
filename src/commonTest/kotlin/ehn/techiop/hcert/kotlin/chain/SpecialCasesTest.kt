package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.debug.DebugChain
import ehn.techiop.hcert.kotlin.chain.impl.*
import ehn.techiop.hcert.kotlin.log.BasicLogger
import ehn.techiop.hcert.kotlin.log.setLogLevel
import ehn.techiop.hcert.kotlin.trust.CoseAdapter
import io.github.aakira.napier.Napier
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SpecialCasesTest : DescribeSpec({

    it("LV null entries in test") {
        setLogLevel(Napier.Level.VERBOSE)
        Napier.base(BasicLogger())


        // from https://github.com/eu-digital-green-certificates/dcc-quality-assurance/tree/main/LV/1.0.0/specialcases
        val input =
            "HC1:NCFOXN%TSMAHN-H3ZSUZK+.V0ET9%6-AH-R61ROR\$SIOOV*I-05/QGKG554QBJ1F/8X*G3M9JUPY0BZW4Z*AK.GNNVR*G0C7PHBO33BC786B*E3-433QBV53XEBW77WNN+FNULJ96B4UN*97\$IJV7776B*D3LL7SZ4ZI00T9UKP0T9WC5PF6846A\$QX76NZ64998T5UEIY0Q\$UPR\$5:NLOEPNRAE69K P4NPDDAJP5DMH1\$4R/S09T./0LWTKD3323UJ0BGJB/S7-SN2H N37J3JFTULJ5CB8X2+36D-I/2DBAJDAJCNB-43 X4VV2 73-E3GG3V20-7TZD5CC9T0H\$/IC74:ZH:PI/E2\$4JY/K9/INE0J0A.G9/G9F:QQ28R3U6/V.*NT*QI%KZYNNEVQ KB+P8\$J3-SY\$NKLACIQ 52564L64W5A 4F4DR+7C218UBRM.SY\$N-P1S29 34S0B8DRFRMLNKNM8LK4NTOI5H- A+EJR-E1WL3T7+AR+*FV3UX-TXEE1PDRT3 8Q/%DWZR2TVXS7-0EV7LK8U44V9:2XDS MNC0V0JGQ8KZCOGXH\$%4O28GZTKOQV50 HT TH"

        val decodingChain =DebugChain.buildVerificationChain(PrefilledCertificateRepository("""-----BEGIN CERTIFICATE-----
MIIBJTCBy6ADAgECAgUAwvEVkzAKBggqhkjOPQQDAjAQMQ4wDAYDVQQDDAVFQy1N
ZTAeFw0yMTA0MjMxMTI3NDhaFw0yMTA1MjMxMTI3NDhaMBAxDjAMBgNVBAMMBUVD
LU1lMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE/OV5UfYrtE140ztF9jOgnux1
oyNO8Bss4377E/kDhp9EzFZdsgaztfT+wvA29b7rSb2EsHJrr8aQdn3/1ynte6MS
MBAwDgYDVR0PAQH/BAQDAgWgMAoGCCqGSM49BAMCA0kAMEYCIQC51XwstjIBH10S
N701EnxWGK3gIgPaUgBN+ljZAs76zQIhAODq4TJ2qAPpFc1FIUOvvlycGJ6QVxNX
EkhRcgdlVfUb
-----END CERTIFICATE-----"""))

        val result = decodingChain.decode(input)

        println(result)
    }

})

/**
 * Does not verify that we know the certificate, but that's not the point of the test anyway
 */

