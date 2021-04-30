package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://github.com/ehn-digital-green-development/ehn-dgc-schema/blob/main/valuesets/vaccine-prophylaxis.json
// 2021-04-27
@Serializable
enum class VaccineType(val value: String) {
    @SerialName("1119305005")
    SARS_COV2_ANTIGEN("1119305005"),

    @SerialName("1119349007")
    SARS_COV2_MRNA("1119349007"),

    @SerialName("J07BX03")
    COVID_19_VACCINES("J07BX03"),

    @SerialName("Unknown")
    UNKNOWN("Unknown");

    companion object {
        fun findByValue(value: String): VaccineType {
            return values().firstOrNull { it.value == value } ?: UNKNOWN
        }
    }
}