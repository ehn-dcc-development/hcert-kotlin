package ehn.techiop.hcert.kotlin.data

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GreenCertificate(
    @SerialName("ver")
    val schemaVersion: String,

    @SerialName("nam")
    val subject: Person,

    @SerialName("dob")
    val dateOfBirth: LocalDate,

    @SerialName("v")
    val vaccinations: List<Vaccination?>? = null,

    @SerialName("r")
    val recoveryStatements: List<RecoveryStatement?>? = null,

    @SerialName("t")
    val tests: List<Test?>? = null,
)
