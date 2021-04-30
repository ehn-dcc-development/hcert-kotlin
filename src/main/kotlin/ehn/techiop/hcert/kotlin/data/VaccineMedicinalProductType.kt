package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://github.com/ehn-digital-green-development/ehn-dgc-schema/blob/main/valuesets/vaccine-medicinal-product.json
// 2021-04-27
@Serializable
enum class VaccineMedicinalProductType(val value: String) {
    @SerialName("EU/1/20/1528")
    COMIRNATY("EU/1/20/1528"),

    @SerialName("EU/1/20/1507")
    MODERNA("EU/1/20/1507"),

    @SerialName("EU/1/21/1529")
    VAXZEVRIA("EU/1/21/1529"),

    @SerialName("EU/1/20/1525")
    JANSSEN("EU/1/20/1525"),

    @SerialName("CVnCoV")
    CVNCOV("CVnCoV"),

    @SerialName("Sputnik-V")
    SPUTNIK_V("Sputnik-V"),

    @SerialName("Convidecia")
    CONVIDECIA("Convidecia"),

    @SerialName("EpiVacCorona")
    EPI_VAC_CORONA("EpiVacCorona"),

    @SerialName("BBIBP-CorV")
    BBIBP_CORV("BBIBP-CorV"),

    @SerialName("Inactivated-SARS-CoV-2-Vero-Cell")
    VERO_CELL("Inactivated-SARS-CoV-2-Vero-Cell"),

    @SerialName("CoronaVac")
    CORONA_VAC("CoronaVac"),

    @SerialName("Covaxin")
    COVAXIN("Covaxin"),

    @SerialName("Unknown")
    UNKNOWN("Unknown");

    companion object {
        fun findByValue(value: String): VaccineMedicinalProductType {
            return values().firstOrNull { it.value == value } ?: UNKNOWN
        }
    }
}