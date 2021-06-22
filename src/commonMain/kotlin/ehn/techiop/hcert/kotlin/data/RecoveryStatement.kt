package ehn.techiop.hcert.kotlin.data

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecoveryStatement(
    @SerialName("tg")
    val target: ValueSetEntryAdapter,

    @SerialName("fr")
    @Serializable(with = LenientLocalDateParser::class)
    val dateOfFirstPositiveTestResult: LocalDate,

    @SerialName("co")
    val country: String,

    @SerialName("is")
    val certificateIssuer: String,

    @SerialName("df")
    @Serializable(with = LenientLocalDateParser::class)
    val certificateValidFrom: LocalDate,

    @SerialName("du")
    @Serializable(with = LenientLocalDateParser::class)
    val certificateValidUntil: LocalDate,

    @SerialName("ci")
    val certificateIdentifier: String,
) {
}
