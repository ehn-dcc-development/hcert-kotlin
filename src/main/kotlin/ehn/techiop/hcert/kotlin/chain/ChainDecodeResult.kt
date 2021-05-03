package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.data.Eudgc

data class ChainDecodeResult(
    val eudgc: Eudgc,
    val step1Cbor: ByteArray,
    val step2Cose: ByteArray,
    val step3Compressed: ByteArray,
    val step4Encoded: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChainDecodeResult

        if (eudgc != other.eudgc) return false
        if (!step1Cbor.contentEquals(other.step1Cbor)) return false
        if (!step2Cose.contentEquals(other.step2Cose)) return false
        if (!step3Compressed.contentEquals(other.step3Compressed)) return false
        if (step4Encoded != other.step4Encoded) return false

        return true
    }

    override fun hashCode(): Int {
        var result = eudgc.hashCode()
        result = 31 * result + step1Cbor.contentHashCode()
        result = 31 * result + step2Cose.contentHashCode()
        result = 31 * result + step3Compressed.contentHashCode()
        result = 31 * result + step4Encoded.hashCode()
        return result
    }
}