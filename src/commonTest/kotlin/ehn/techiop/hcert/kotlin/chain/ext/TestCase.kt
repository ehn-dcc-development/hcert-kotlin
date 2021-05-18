package ehn.techiop.hcert.kotlin.chain.ext

import ehn.techiop.hcert.kotlin.data.GreenCertificate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestCase(
    @SerialName("JSON")
    val eudgc: GreenCertificate? = null,
    @SerialName("CBOR")
    val cborHex: String? = null,
    @SerialName("COSE")
    val coseHex: String? = null,
    @SerialName("COMPRESSED")
    val compressedHex: String? = null,
    @SerialName("BASE45")
    val base45: String? = null,
    @SerialName("PREFIX")
    val base45WithPrefix: String? = null,
    @SerialName("2DCODE")
    val qrCodePng: String? = null,
    @SerialName("TESTCTX")
    val context: TestContext,
    @SerialName("EXPECTEDRESULTS")
    val expectedResult: TestExpectedResults,
)