package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://ec.europa.eu/health/sites/health/files/ehealth/docs/digital-green-certificates_dt-specifications_en.pdf
@Serializable
enum class DiseaseTargetType(val value: String) {
    @SerialName("840539006")
    COVID19("840539006");

    companion object {
        fun findByValue(value: String): DiseaseTargetType {
            return values().firstOrNull { it.value == value } ?: COVID19
        }
    }
}