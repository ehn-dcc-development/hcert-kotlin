package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.Date

/**
 * This class contains "safe" objects for Javascript,
 * i.e. ones that can be serialized without name mangling.
 * One solution would be to annotate all exported classes with
 * [JsExport], but that won't work together with [Serializable]...
 */
@Serializable
data class RecoveryStatementJs(
    @SerialName("tg")
    val target: ValueSetEntryAdapter,

    @SerialName("fr")
    @Serializable(with = JsDateSerializer::class)
    val dateOfFirstPositiveTestResult: Date,

    @SerialName("co")
    val country: String,

    @SerialName("is")
    val certificateIssuer: String,

    @SerialName("df")
    @Serializable(with = JsDateSerializer::class)
    val certificateValidFrom: Date,

    @SerialName("du")
    @Serializable(with = JsDateSerializer::class)
    val certificateValidUntil: Date,

    @SerialName("ci")
    val certificateIdentifier: String,
) {
    constructor(recovery: RecoveryStatement) : this(
        recovery.target,
        Date(recovery.dateOfFirstPositiveTestResult.toString()),
        recovery.country,
        recovery.certificateIssuer,
        Date(recovery.certificateValidFrom.toString()),
        Date(recovery.certificateValidUntil.toString()),
        recovery.certificateIdentifier
    )
}
