package ehn.techiop.hcert.kotlin.data

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecoveryStatement(
    @SerialName("tg")
    val target: ValueSetEntryAdapter,

    @SerialName("fr")
    val dateOfFirstPositiveTestResult: LocalDate,

    @SerialName("co")
    val country: String,

    @SerialName("is")
    val certificateIssuer: String,

    @SerialName("df")
    val certificateValidFrom: LocalDate,

    @SerialName("du")
    val certificateValidUntil: LocalDate,

    @SerialName("ci")
    val certificateIdentifier: String,
)