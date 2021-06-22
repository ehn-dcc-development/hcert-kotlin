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
    val dateOfBirthString: String,

    @SerialName("v")
    val vaccinations: List<Vaccination?>? = null,

    @SerialName("r")
    val recoveryStatements: List<RecoveryStatement?>? = null,

    @SerialName("t")
    val tests: List<Test?>? = null,
) {

    /**
     * For [dateOfBirthString] ("dob"), month and day are optional in eu-dcc-schema 1.2.1,
     * so we may not be able to get a valid [LocalDate] from it.
     * Be lenient, i.e. strip a timestamp, if it is included
     */
    val dateOfBirth: LocalDate?
        get() = try {
            LocalDate.parse(dateOfBirthString.substringBefore("T"))
        } catch (e: Throwable) {
            null
        }


}
