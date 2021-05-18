package ehn.techiop.hcert.kotlin.trust

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


const val oidTest = "1.3.6.1.4.1.0.1847.2021.1.1"
const val oidVaccination = "1.3.6.1.4.1.0.1847.2021.1.2"
const val oidRecovery = "1.3.6.1.4.1.0.1847.2021.1.3"


@Serializable
enum class ContentType {
    @SerialName("t")
    TEST,

    @SerialName("v")
    VACCINATION,

    @SerialName("r")
    RECOVERY;
}
