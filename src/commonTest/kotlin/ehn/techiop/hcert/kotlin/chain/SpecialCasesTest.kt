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
            "HC1:6BFOXN*TS0BI\$ZDFRH%JJ*+CMSL6TQ0IIGRS 43JUB/EB6QIKOA.TMEY4JN6R2N:UC*GPXS40 LHZA KEJ*G%9DJ6K1AD1WMN+I0JK1WLB4D/IKFHHNII54OIOA+CL7DJYULX6IV5JQDKP29/DCO9B1ANOPD-JELHQC K1U7C KPLI8J4RK46YBBOA4N4N\$K.SS\$FKV\$K0MKYX0U/VFP1ZBQ.SSZ%P-RQ*\$K3\$OHBW24FAL86H0YQCWMD*\$K8KG+9RR\$F+ F%J00N89M40%KLR2A KZ*U0I1-I0*OC6H0/VMNPM Q5TM8*N9 I2.8Q4A7E:7LYPHTQ*88E20JSISKE MCAOI8%M6YF/PK \$NA+QT\$K1RKDPI.SSI%KH NY9LZPK1\$I8%MFNIY5LE09Z.25280EQNAV4DRCEIC.UKMIT15K3449JFRMLNKNM8JI0JPGB7H66TZ-M.LQA-8 .7QZJDN7MWKD*MD9U-B3\$%A08WQ:618JE3FBXAYFWX:GF8ST\$1R0E\$ZN5.SJII6K3I0P-042ON//V3DW1APL4U14JSRK2KG"

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

        println(Json { prettyPrint = true }.encodeToString(result))
    }

})

/**
 * Does not verify that we know the certificate, but that's not the point of the test anyway
 */

