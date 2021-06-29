package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.impl.DefaultBase45Service
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCborService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCompressorService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultContextIdentifierService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultCwtService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultHigherOrderValidationService
import ehn.techiop.hcert.kotlin.chain.impl.DefaultSchemaValidationService
import ehn.techiop.hcert.kotlin.trust.CoseAdapter
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class SpecialCasesTest : DescribeSpec({

    it("LV null entries in test") {
        // from https://github.com/eu-digital-green-certificates/dcc-quality-assurance/tree/main/LV/1.0.0/specialcases
        val input =
            "HC1:NCF7%AW08+J2DO3K6CLQHRU3J.KTWO\$I8MOFG:NS4FC7SCJK3W8*LPCL9 WI6RAPEUJO9YOI5 IL8R:UE/U0T+2YLKHGJL.K\$F70SK27WG%GH 1LI8BSQ.1M%BOL%VOOM*D3T2TG+NG6DZBDMDR.YFKCLDQ1/04O%AP7DMJN6PR4:V\$OBETBF\$HJ:3GXBNZ6I1H6NCZ81ZZI.SME.SP0RW\$S\$.KV2VKEB5D3AXEB31HGJR9FWCD%6PP75MJR*3KUHOJS58U5D\$CT9U%VEV3SHFPEE0/U7%SVA/VZJ45HMHGLSB1VJR\$P2EZR+1POJEO/0LHRWWGPARMO8Z5CGAGPR9.KDQ5Q0:TSLGDVG18DAPNNXN289*WQ:Q7DTJOABHWDZMTCO6D14F66T25A/7S4KJEBUZP-XS-DK4QFJ 45PEOG1KRJ/00A\$PWC1/D1G30XUK.WNYPK8M8PC5AZM1XK/A2UWSA5OSU7\$VFQ64\$.2 3T+60VD4+WRCWTI%FOUVMXQ\$VN/1EL.F4:NW 3T LA28 4LEOD7-L0ZENBPO6D-DW*VLM5BE4H7CSD7O%:T0%V*RS7\$TTOTUQV44VR4AV7JGPESYHF2"

        val decodingChain = Chain(
            DefaultHigherOrderValidationService(),
            DefaultSchemaValidationService(),
            DefaultCborService(),
            DefaultCwtService(),
            NullCoseService(),
            DefaultCompressorService(),
            DefaultBase45Service(),
            DefaultContextIdentifierService()
        )

        val result = decodingChain.decode(input)
        result.verificationResult.error shouldBe null
        result.chainDecodeResult.eudgc shouldNotBe null
        println(result.chainDecodeResult.eudgc)
    }

})

/**
 * Does not verify that we know the certificate, but that's not the point of the test anyway
 */
private class NullCoseService : CoseService {

    override fun encode(input: ByteArray) = throw NotImplementedError()

    override fun decode(input: ByteArray, verificationResult: VerificationResult) =
        CoseAdapter(input).getContent()

}
