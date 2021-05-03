package ehn.techiop.hcert.kotlin.chain.ext

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestExpectedResults(
    @SerialName("EXPECTEDVALIDOBJECT")
    val validObjectForGeneration: Boolean? = null,
    @SerialName("EXPECTEDSCHEMAVALIDATION")
    val verifySchemaValidation: Boolean? = null,
    @SerialName("EXPECTEDENCODE")
    val encodeForGeneration: Boolean? = null,
    @SerialName("EXPECTEDDECODE")
    val verifyCborDecode: Boolean? = null,
    @SerialName("EXPECTEDVERIFY")
    val verifyCoseSignature: Boolean? = null,
    @SerialName("EXPECTEDUNPREFIX")
    val verifyPrefix: Boolean? = null,
    @SerialName("EXPECTEDVALIDJSON")
    val verifyJson: Boolean? = null,
    @SerialName("EXPECTEDB45DECODE")
    val verifyBase45Decode: Boolean? = null,
    @SerialName("EXPECTEDPICTUREDECODE")
    val verifyQrDecode: Boolean? = null,
    @SerialName("EXPTECTEDEXPIRED")
    val expired: Boolean? = null // TODO when true, then expects an error, on other fields in expects success
)