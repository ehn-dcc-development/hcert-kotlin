package ehn.techiop.hcert.kotlin.chain

data class ChainResult(
    val step1Cbor: ByteArray,
    val step2Cose: ByteArray,
    val step3Compressed: ByteArray,
    val step4Encoded: String,
    val step5Prefixed: String,
)