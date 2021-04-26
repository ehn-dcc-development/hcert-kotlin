package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://ec.europa.eu/health/sites/health/files/ehealth/docs/digital-green-certificates_dt-specifications_en.pdf
@Serializable
enum class VaccineType(val value: String) {
    @SerialName("1119305005")
    SARS_COV2_ANTIGEN("1119305005"),

    @SerialName("1119349007")
    SARS_COV2_MRNA("1119349007"),

    @SerialName("J07BX03")
    UNKNOWN("J07BX03");

    companion object {
        fun findByValue(value: String): VaccineType {
            return values().firstOrNull { it.value == value } ?: UNKNOWN
        }
    }
}