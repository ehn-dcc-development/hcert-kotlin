package ehn.techiop.hcert.kotlin.data

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsName

@Serializable
data class RecoveryStatement(
    @SerialName("tg")
    @JsName("target")
    val target: ValueSetEntryAdapter,

    @SerialName("fr")
    @Serializable(with = LenientLocalDateParser::class)
    @JsName("dateOfFirstPositiveTestResult")
    val dateOfFirstPositiveTestResult: LocalDate,

    @SerialName("co")
    @JsName("country")
    val country: String,

    @SerialName("is")
    @JsName("certificateIssuer")
    val certificateIssuer: String,

    @SerialName("df")
    @Serializable(with = LenientLocalDateParser::class)
    @JsName("certificateValidFrom")
    val certificateValidFrom: LocalDate,

    @SerialName("du")
    @Serializable(with = LenientLocalDateParser::class)
    @JsName("certificateValidUntil")
    val certificateValidUntil: LocalDate,

    @SerialName("ci")
    @JsName("certificateIdentifier")
    val certificateIdentifier: String,
) {
}
