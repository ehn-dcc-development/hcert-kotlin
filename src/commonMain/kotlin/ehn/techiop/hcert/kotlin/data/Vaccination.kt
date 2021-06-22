package ehn.techiop.hcert.kotlin.data

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Vaccination(
    @SerialName("tg")
    val target: ValueSetEntryAdapter,

    @SerialName("vp")
    val vaccine: ValueSetEntryAdapter,

    @SerialName("mp")
    val medicinalProduct: ValueSetEntryAdapter,

    @SerialName("ma")
    val authorizationHolder: ValueSetEntryAdapter,

    @SerialName("dn")
    val doseNumber: Int,

    @SerialName("sd")
    val doseTotalNumber: Int,

    @SerialName("dt")
    @Serializable(with = LenientLocalDateParser::class)
    val date: LocalDate,

    @SerialName("co")
    val country: String,

    @SerialName("is")
    val certificateIssuer: String,

    @SerialName("ci")
    val certificateIdentifier: String,
)