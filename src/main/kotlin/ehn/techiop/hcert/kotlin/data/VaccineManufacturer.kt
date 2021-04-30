package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://github.com/ehn-digital-green-development/ehn-dgc-schema/blob/main/valuesets/vaccine-mah-manf.json
// 2021-04-27
@Serializable
enum class VaccineManufacturer(val value: String) {
    @SerialName("ORG-100001699")
    ASTRA_ZENECA("ORG-100001699"),

    @SerialName("ORG-100030215")
    BIONTECH("ORG-100030215"),

    @SerialName("ORG-100001417")
    JANSSEN("ORG-100001417"),

    @SerialName("ORG-100031184")
    MODERNA("ORG-100031184"),

    @SerialName("ORG-100006270")
    CUREVAC("ORG-100006270"),

    @SerialName("ORG-100013793")
    CANSINO("ORG-100013793"),

    @SerialName("ORG-100020693")
    SINOPHARM_CHINA("ORG-100020693"),

    @SerialName("ORG-100010771")
    SINOPHARM_EUROPE("ORG-100010771"),

    @SerialName("ORG-100024420")
    SINOPHARM_SHENZHEN("ORG-100024420"),

    @SerialName("ORG-100032020")
    NOVAVAX("ORG-100032020"),

    @SerialName("Gamaleya-Research-Institute")
    GAMALEYA("Gamaleya-Research-Institute"),

    @SerialName("Vector-Institute")
    VECTOR_INSTITUTE("Vector-Institute"),

    @SerialName("Sinovac-Biotech")
    SINOVAC("Sinovac-Biotech"),

    @SerialName("Bharat-Biotech")
    BHARAT("Bharat-Biotech"),

    @SerialName("Unknown")
    UNKNOWN("Unknown");

    companion object {
        fun findByValue(value: String): VaccineManufacturer {
            return values().firstOrNull { it.value == value } ?: UNKNOWN
        }
    }
}