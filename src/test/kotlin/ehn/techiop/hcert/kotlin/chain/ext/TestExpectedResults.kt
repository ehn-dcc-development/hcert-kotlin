package ehn.techiop.hcert.kotlin.chain.ext

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestExpectedResults(
    @SerialName("EXPECTEDVALIDOBJECT")
    val verifySchemaGeneration: Boolean? = null,
    @SerialName("EXPECTEDSCHEMAVALIDATION")
    val verifySchemaValidation: Boolean? = null,
    @SerialName("EXPECTEDENCODE")
    val verifyEncodeGeneration: Boolean? = null,
    @SerialName("EXPECTEDDECODE")
    val verifyCborDecode: Boolean? = null,
    @SerialName("EXPECTEDVERIFY")
    val verifyCoseSignature: Boolean? = null,
    @SerialName("EXPECTEDUNPREFIX")
    val verifyPrefix: Boolean? = null,
    @SerialName("EXPECTEDVALIDJSON")
    val verifyJson: Boolean? = null,
    @SerialName("EXPECTEDCOMPRESSION")
    val verifyCompression: Boolean? = null, // TODO new property
    @SerialName("EXPECTEDB45DECODE")
    val verifyBase45Decode: Boolean? = null,
    @SerialName("EXPECTEDPICTUREDECODE")
    val verifyQrDecode: Boolean? = null,
    @SerialName("EXPECTEDEXPIRATIONCHECK")
    val expired: Boolean? = null // TODO when true, then expect an error, but other fields expect success when set
)