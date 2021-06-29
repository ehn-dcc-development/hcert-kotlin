package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.data.JsDateSerializer
import kotlinx.serialization.Serializable
import kotlin.js.Date

/**
 * This class contains "safe" objects for Javascript,
 * i.e. ones that can be serialized without name mangling.
 * One solution would be to annotate all exported classes with
 * [JsExport], but that won't work together with [Serializable]...
 */
@Serializable
data class VerificationResultJs(
    @Serializable(with = JsDateSerializer::class)
    val expirationTime: Date? = null,
    @Serializable(with = JsDateSerializer::class)
    val issuedAt: Date? = null,
    val issuer: String? = null,
    @Serializable(with = JsDateSerializer::class)
    val certificateValidFrom: Date? = null,
    @Serializable(with = JsDateSerializer::class)
    val certificateValidUntil: Date? = null,
    val certificateValidContent: Array<String>? = null,
    val certificateSubjectCountry: String? = null,
    val content: Array<String>? = null,
    val error: String? = null
) {
    constructor(result: VerificationResult) : this(
        result.expirationTime?.let { Date(it.toString()) },
        result.issuedAt?.let { Date(it.toString()) },
        result.issuer,
        result.certificateValidFrom?.let { Date(it.toString()) },
        result.certificateValidUntil?.let { Date(it.toString()) },
        result.certificateValidContent.map { it.name }.toTypedArray(),
        result.certificateSubjectCountry,
        result.content.map { it.name }.toTypedArray(),
        result.error?.toString()
    )
}