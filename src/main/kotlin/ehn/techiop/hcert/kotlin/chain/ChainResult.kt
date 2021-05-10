package ehn.techiop.hcert.kotlin.chain

data class ChainResult(
    val step1Cwt: ByteArray,
    val step2Cose: ByteArray,
    val step3Compressed: ByteArray,
    val step4Encoded: String,
    val step5Prefixed: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChainResult

        if (!step1Cwt.contentEquals(other.step1Cwt)) return false
        if (!step2Cose.contentEquals(other.step2Cose)) return false
        if (!step3Compressed.contentEquals(other.step3Compressed)) return false
        if (step4Encoded != other.step4Encoded) return false
        if (step5Prefixed != other.step5Prefixed) return false

        return true
    }

    override fun hashCode(): Int {
        var result = step1Cwt.contentHashCode()
        result = 31 * result + step2Cose.contentHashCode()
        result = 31 * result + step3Compressed.contentHashCode()
        result = 31 * result + step4Encoded.hashCode()
        result = 31 * result + step5Prefixed.hashCode()
        return result
    }
}