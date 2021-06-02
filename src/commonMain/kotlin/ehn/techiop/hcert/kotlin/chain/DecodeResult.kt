package ehn.techiop.hcert.kotlin.chain

import kotlinx.serialization.Serializable

@Serializable
data class DecodeResult(
    val verificationResult: VerificationResult,
    val chainDecodeResult: ChainDecodeResult
)