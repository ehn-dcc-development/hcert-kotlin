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
            "HC1:NCFRW2BM75VOO10WHHXQQHT2AB8I8AT6R97J.Z9*QGMXV:I09W4-S8*Q6-\$9 WI %IT*LASA4%R332QP0%T04RJOOGQG4%/DAUG6*54LH9PK1HEI:04KN3\$TUK0//61WV9*3SVN62O3CSLB40\$0ST0KFQN\$QF9LJ\$12YT/8GQQJGHHZ-B1P79:GJ6H1J6PPDR911/0W.2WZ3N*2XEBMI9% MK22W8HRR3\$C2DQ4HRH: 2ZW7V-68.FR1DUD0/+N 71T13FTCJRM\$QLFP7O0J+YA+2F/WB9%GB\$9O\$SQF45LKYJ26BPO.DH45\$N2+00 ASKFMQ6ACHB03P.32XR1FRSQ0NJRFJTH870%CECS1TQR882QEPB%MBRSM3N/SH8 S:BW:MEX9EVVN/PEZ 6%AT137.UTEDLXQEVUM/B7XYQ3HFA74\$HRZ%PIHD8S3%PJHF6EIASNHSU1:I2MTI-7PNXKZHFAL5DNG4FP\$N19 6N 6E34A41 39M-A%39RWB-:2EO4MYP79QP7A1PNHSIJ7JUULS1TR024-MS/UO3R+EWJLVET2..7:5U*MVLHJRT1.03:5RY\$R E9\$5H.O1%1H"

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

