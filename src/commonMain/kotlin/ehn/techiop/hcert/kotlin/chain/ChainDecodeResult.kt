package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.data.GreenCertificate
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class ChainDecodeResult(
    val eudgc: GreenCertificate?,
    val step0rawEuGcc: String?,
    val step1Cwt: ByteArray?,
    val step2Cose: ByteArray?,
    val step3Compressed: ByteArray?,
    val step4Encoded: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        other?.let { if (this::class != other::class) return false }

        other as ChainDecodeResult

        if (eudgc != other.eudgc) return false
        if (!step0rawEuGcc.contentEquals(other.step0rawEuGcc)) return false
        if (!step1Cwt.contentEquals(other.step1Cwt)) return false
        if (!step2Cose.contentEquals(other.step2Cose)) return false
        if (!step3Compressed.contentEquals(other.step3Compressed)) return false
        if (step4Encoded != other.step4Encoded) return false

        return true
    }

    override fun hashCode(): Int {
        var result = eudgc.hashCode()
        result = 31 * result + step0rawEuGcc.hashCode()
        result = 31 * result + step1Cwt.contentHashCode()
        result = 31 * result + step2Cose.contentHashCode()
        result = 31 * result + step3Compressed.contentHashCode()
        result = 31 * result + step4Encoded.hashCode()
        return result
    }

    fun toJson() = Json.encodeToJsonElement(this)
    fun toJsonString() = toJson().toString()

}
