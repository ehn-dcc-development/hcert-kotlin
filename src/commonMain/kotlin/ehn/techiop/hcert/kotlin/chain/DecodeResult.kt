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
) {

    fun toJson(anonymized: Boolean = false) = json.encodeToJsonElement(
        if (anonymized) (DecodeResult(
            verificationResult,
            chainDecodeResult.anonymizedCopy
        )) else this
    )

    fun toJsonString(anonymized: Boolean = false) = json.encodeToString(toJson(anonymized))
    override fun toString() = toJsonString()
}