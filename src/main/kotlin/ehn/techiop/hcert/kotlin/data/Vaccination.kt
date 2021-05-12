package ehn.techiop.hcert.kotlin.data

import ehn.techiop.hcert.data.VaccinationEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,

    @SerialName("co")
    val country: String,

    @SerialName("is")
    val certificateIssuer: String,

    @SerialName("ci")
    val certificateIdentifier: String,
) {
    /*fun toEuSchema() = VaccinationEntry().apply {
        tg = target.key
        vp = vaccine.key
        mp = medicinalProduct.key
        ma = authorizationHolder.key
        dn = doseNumber
        sd = doseTotalNumber
        dt = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        co = country
        `is` = certificateIssuer
        ci = certificateIdentifier
    }

    companion object {
        @JvmStatic
        fun fromEuSchema(it: VaccinationEntry) = Vaccination(
            target = ValueSetHolder.INSTANCE.find("disease-agent-targeted", it.tg),
            vaccine = ValueSetHolder.INSTANCE.find("sct-vaccines-covid-19", it.vp),
            medicinalProduct = ValueSetHolder.INSTANCE.find("vaccines-covid-19-names", it.mp),
            authorizationHolder = ValueSetHolder.INSTANCE.find("vaccines-covid-19-auth-holders", it.ma),
            doseNumber = it.dn,
            doseTotalNumber = it.sd,
            date = LocalDate.parse(it.dt, DateTimeFormatter.ISO_LOCAL_DATE),
            country = it.co,
            certificateIssuer = it.`is`,
            certificateIdentifier = it.ci
        )
    }*/
}