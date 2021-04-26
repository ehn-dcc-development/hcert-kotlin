package ehn.techiop.hcert.kotlin.data

import ehn.techiop.hcert.data.VaccinationEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class Vaccination(
    @SerialName("tg")
    val target: DiseaseTargetType,

    @SerialName("vp")
    val vaccine: VaccineType,

    @SerialName("mp")
    val medicinalProduct: VaccineMedicinalProductType,

    @SerialName("ma")
    val authorizationHolder: VaccineManufacturer,

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
    companion object {
        @JvmStatic
        fun fromEuSchema(it: VaccinationEntry) = Vaccination(
            target = DiseaseTargetType.findByValue(it.tg.value()),
            vaccine = VaccineType.findByValue(it.vp.value()),
            medicinalProduct = VaccineMedicinalProductType.findByValue(it.mp.value()),
            authorizationHolder = VaccineManufacturer.findByValue(it.ma.value()),
            doseNumber = it.dn,
            doseTotalNumber = it.sd,
            date = LocalDate.parse(it.dt, DateTimeFormatter.ISO_DATE),
            country = it.co,
            certificateIssuer = it.`is`,
            certificateIdentifier = it.ci
        )
    }
}