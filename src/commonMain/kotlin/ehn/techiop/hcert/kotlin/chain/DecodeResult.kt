package ehn.techiop.hcert.kotlin.chain

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement


private val json = Json { prettyPrint = true }

@Serializable
data class DecodeResult(
    val verificationResult: VerificationResult,
    val chainDecodeResult: ChainDecodeResult
){

    fun toJson() = json.encodeToJsonElement(this)
    fun toJsonString() = json.encodeToString(this)
    override fun toString()=toJsonString()
}