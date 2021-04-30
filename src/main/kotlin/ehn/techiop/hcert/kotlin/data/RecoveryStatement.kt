package ehn.techiop.hcert.kotlin.data

import ehn.techiop.hcert.data.RecoveryEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class RecoveryStatement(
    @SerialName("tg")
    val target: DiseaseTargetType,

    @SerialName("fr")
    @Serializable(with = LocalDateSerializer::class)
    val dateOfFirstPositiveTestResult: LocalDate,

    @SerialName("co")
    val country: String,

    @SerialName("is")
    val certificateIssuer: String,

    @SerialName("df")
    @Serializable(with = LocalDateSerializer::class)
    val certificateValidFrom: LocalDate,

    @SerialName("du")
    @Serializable(with = LocalDateSerializer::class)
    val certificateValidUntil: LocalDate,

    @SerialName("ci")
    val certificateIdentifier: String,
) {
    companion object {
        @JvmStatic
        fun fromEuSchema(it: RecoveryEntry) = RecoveryStatement(
            target = DiseaseTargetType.findByValue(it.tg),
            dateOfFirstPositiveTestResult = LocalDate.parse(it.fr, DateTimeFormatter.ISO_DATE),
            country = it.co,
            certificateIssuer = it.`is`,
            certificateValidFrom = LocalDate.parse(it.df, DateTimeFormatter.ISO_DATE),
            certificateValidUntil = LocalDate.parse(it.du, DateTimeFormatter.ISO_DATE),
            certificateIdentifier = it.ci
        )
    }
}