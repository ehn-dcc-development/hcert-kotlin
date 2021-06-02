package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.data.GreenCertificate
import kotlinx.serialization.Serializable

@Serializable
data class DecodeJsResult(
    val isValid: Boolean,
    val error: Error?,
    val metaInformation: VerificationResult,
    val greenCertificate: GreenCertificate?,
) {
    constructor(extResult: DecodeResult) : this(
        extResult.verificationResult.error == null,
        extResult.verificationResult.error,
        extResult.verificationResult,
        extResult.chainDecodeResult.eudgc,
    )
}