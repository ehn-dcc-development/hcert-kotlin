package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.data.GreenCertificateJs
import kotlinx.serialization.Serializable

/**
 * This class contains "safe" objects for Javascript,
 * i.e. ones that can be serialized without name mangling.
 * One solution would be to annotate all exported classes with
 * [JsExport], but that won't work together with [Serializable]...
 */
@Serializable
data class DecodeResultJs(
    val isValid: Boolean,
    val error: String?,
    val metaInformation: VerificationResultJs,
    val greenCertificate: GreenCertificateJs?,
) {
    constructor(extResult: DecodeResult) : this(
        extResult.verificationResult.error == null,
        extResult.verificationResult.error?.name,
        VerificationResultJs(extResult.verificationResult),
        extResult.chainDecodeResult.eudgc?.let { GreenCertificateJs(it) },
    )
}