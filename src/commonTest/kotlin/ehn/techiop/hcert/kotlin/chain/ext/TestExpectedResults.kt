package ehn.techiop.hcert.kotlin.chain.ext

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestExpectedResults(
    @SerialName("EXPECTEDVALIDOBJECT")
    val schemaGeneration: Boolean? = null,
    @SerialName("EXPECTEDSCHEMAVALIDATION")
    val schemaValidation: Boolean? = null,
    @SerialName("EXPECTEDENCODE")
    val encodeGeneration: Boolean? = null,
    @SerialName("EXPECTEDDECODE")
    val cborDecode: Boolean? = null,
    @SerialName("EXPECTEDVERIFY")
    val coseSignature: Boolean? = null,
    @SerialName("EXPECTEDUNPREFIX")
    val prefix: Boolean? = null,
    @SerialName("EXPECTEDVALIDJSON")
    val json: Boolean? = null,
    @SerialName("EXPECTEDCOMPRESSION")
    val compression: Boolean? = null,
    @SerialName("EXPECTEDB45DECODE")
    val base45Decode: Boolean? = null,
    @SerialName("EXPECTEDPICTUREDECODE")
    val qrDecode: Boolean? = null,
    @SerialName("EXPECTEDEXPIRATIONCHECK")
    val expirationCheck: Boolean? = null,
    @SerialName("EXPECTEDKEYUSAGE")
    val keyUsage: Boolean? = null,
)