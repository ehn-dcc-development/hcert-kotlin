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
) {

    /*
    fun toEuSchema() = Eudgc().apply {
        ver = schemaVersion
        nam = subject.toEuSchema()
        dob = dateOfBirth.format(DateTimeFormatter.ISO_LOCAL_DATE)
        v = vaccinations?.filterNotNull()?.map { it.toEuSchema() }?.toList()?.ifEmpty { null }
        r = recoveryStatements?.filterNotNull()?.map { it.toEuSchema() }?.toList()?.ifEmpty { null }
        t = tests?.filterNotNull()?.map { it.toEuSchema() }?.toList()?.ifEmpty { null }
    }

    companion object {

        @JvmStatic
        fun fromEuSchema(input: Eudgc): GreenCertificate? {
            if (input.nam == null || input.ver == null || input.dob == null) {
                return null
            }
            return try {
                GreenCertificate(
                    schemaVersion = input.ver,
                    subject = Person.fromEuSchema(input.nam),
                    dateOfBirth = LocalDate.parse(input.dob, DateTimeFormatter.ISO_LOCAL_DATE),
                    vaccinations = input.v?.let { it.map { v -> Vaccination.fromEuSchema(v) } },
                    recoveryStatements = input.r?.let { it.map { r -> RecoveryStatement.fromEuSchema(r) } },
                    tests = input.t?.let { it.map { t -> Test.fromEuSchema(t) } },
                )
            } catch (e: Throwable) {
                null
            }
        }
    }*/
}
