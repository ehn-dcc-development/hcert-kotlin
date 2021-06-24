package ehn.techiop.hcert.kotlin.chain

import kotlinx.serialization.Serializable

/**
 * This class contains "safe" objects for Javascript,
 * i.e. ones that can be serialized without name mangling.
 * One solution would be to annotate all exported classes with
 * [JsExport], but that won't work together with [Serializable]...
 */
@Serializable
data class VerificationResultJs(
    val expirationTime: String? = null,
    val issuedAt: String? = null,
    val issuer: String? = null,
    val certificateValidFrom: String? = null,
    val certificateValidUntil: String? = null,
    val certificateValidContent: Array<String>? = null,
    val content: Array<String>? = null,
    val error: String? = null
) {
    constructor(result: VerificationResult) : this(
        result.expirationTime?.toString(),
        result.issuedAt?.toString(),
        result.issuer,
        result.certificateValidFrom?.toString(),
        result.certificateValidUntil?.toString(),
        result.certificateValidContent.map { it.name }.toTypedArray(),
        result.content.map { it.name }.toTypedArray(),
        result.error?.toString()
    )
}