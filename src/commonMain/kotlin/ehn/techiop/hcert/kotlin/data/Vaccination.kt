package ehn.techiop.hcert.kotlin.data

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsName

@Serializable
data class Vaccination(
    @SerialName("tg")
    @JsName("target")
    val target: ValueSetEntryAdapter,

    @SerialName("vp")
    @JsName("vaccine")
    val vaccine: ValueSetEntryAdapter,

    @SerialName("mp")
    @JsName("medicinalProduct")
    val medicinalProduct: ValueSetEntryAdapter,

    @SerialName("ma")
    @JsName("authorizationHolder")
    val authorizationHolder: ValueSetEntryAdapter,

    @SerialName("dn")
    @JsName("doseNumber")
    val doseNumber: Int,

    @SerialName("sd")
    @JsName("doseTotalNumber")
    val doseTotalNumber: Int,

    @SerialName("dt")
    @Serializable(with = LenientLocalDateParser::class)
    @JsName("date")
    val date: LocalDate,

    @SerialName("co")
    @JsName("country")
    val country: String,

    @SerialName("is")
    @JsName("certificateIssuer")
    val certificateIssuer: String,

    @SerialName("ci")
    @JsName("certificateIdentifier")
    val certificateIdentifier: String,
)