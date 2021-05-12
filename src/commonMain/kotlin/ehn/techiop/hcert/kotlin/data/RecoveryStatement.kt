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
) {

    /*
    fun toEuSchema() = RecoveryEntry().apply {
        tg = target.key
        fr = dateOfFirstPositiveTestResult.format(DateTimeFormatter.ISO_LOCAL_DATE)
        co = country
        `is` = certificateIssuer
        df = certificateValidFrom.format(DateTimeFormatter.ISO_LOCAL_DATE)
        du = certificateValidUntil.format(DateTimeFormatter.ISO_LOCAL_DATE)
        ci = certificateIdentifier
    }

    companion object {
        @JvmStatic
        fun fromEuSchema(it: RecoveryEntry) = RecoveryStatement(
            target = ValueSetHolder.INSTANCE.find("disease-agent-targeted", it.tg),
            dateOfFirstPositiveTestResult = LocalDate.parse(it.fr, DateTimeFormatter.ISO_LOCAL_DATE),
            country = it.co,
            certificateIssuer = it.`is`,
            certificateValidFrom = LocalDate.parse(it.df, DateTimeFormatter.ISO_LOCAL_DATE),
            certificateValidUntil = LocalDate.parse(it.du, DateTimeFormatter.ISO_LOCAL_DATE),
            certificateIdentifier = it.ci
        )
    }*/
}