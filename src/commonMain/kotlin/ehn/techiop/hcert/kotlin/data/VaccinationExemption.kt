package ehn.techiop.hcert.kotlin.data

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VaccinationExemption(
    @SerialName("tg")
    val target: ValueSetEntryAdapter,

    @SerialName("du")
    @Serializable(with = LenientLocalDateParser::class)
    val date: LocalDate,

    @SerialName("co")
    val country: String,

    @SerialName("is")
    val certificateIssuer: String,

    @SerialName("ci")
    val certificateIdentifier: String,
)