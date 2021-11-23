package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.data.GreenCertificate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

object Base64EncodeSerializer : KSerializer<ByteArray> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Base64EncodedByteArray", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ByteArray = decoder.decodeString().fromBase64()
    override fun serialize(encoder: Encoder, value: ByteArray) = encoder.encodeString(value.asBase64())
}

@Serializable
data class ChainDecodeResult(
    val errors: List<Error>?,
    val eudgc: GreenCertificate?,
    val step0rawEuGcc: String?,
    @Serializable(with = Base64EncodeSerializer::class)
    val step1Cwt: ByteArray?,
    @Serializable(with = Base64EncodeSerializer::class)
    val step2Cose: ByteArray?,
    @Serializable(with = Base64EncodeSerializer::class)
    val step3Compressed: ByteArray?,
    val step4Encoded: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        other?.let { if (this::class != other::class) return false }

        other as ChainDecodeResult

        if (errors != other.errors) return false
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

    @Transient
    val anonymizedCopy by lazy {
        ChainDecodeResult(errors, eudgc?.anonymizedCopy, "***", step1Cwt, step2Cose, step3Compressed, step4Encoded)
    }

}
