package ehn.techiop.hcert.kotlin.data

import ehn.techiop.hcert.data.Eudcc
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    constructor(
        schemaVersion: String,
        subject: Person,
        dateOfBirth: LocalDate,
        vaccinations: List<Vaccination?>?,
        recoveryStatements: List<RecoveryStatement?>?,
        tests: List<Test?>?
    ) : this(
        schemaVersion, subject,
        dateOfBirth.format(DateTimeFormatter.ISO_LOCAL_DATE),
        vaccinations,
        recoveryStatements,
        tests
    )

    /**
     * For [dateOfBirthString] ("dob"), month and day are optional in eu-dcc-schema 1.2.1,
     * so we may not be able to get a valid [LocalDate] from it.
     */
    @Serializable(with = LocalDateSerializer::class)
    val dateOfBirth: LocalDate?
        get() = try {
            LocalDate.parse(dateOfBirthString, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Throwable) {
            null
        }


    fun toEuSchema() = Eudcc().apply {
        ver = schemaVersion
        nam = subject.toEuSchema()
        dob = dateOfBirthString
        v = vaccinations?.filterNotNull()?.map { it.toEuSchema() }?.toList()?.ifEmpty { null }
        r = recoveryStatements?.filterNotNull()?.map { it.toEuSchema() }?.toList()?.ifEmpty { null }
        t = tests?.filterNotNull()?.map { it.toEuSchema() }?.toList()?.ifEmpty { null }
    }

    companion object {

        @JvmStatic
        fun fromEuSchema(input: Eudcc): GreenCertificate? {
            if (input.nam == null || input.ver == null || input.dob == null) {
                return null
            }
            return try {
                GreenCertificate(
                    schemaVersion = input.ver,
                    subject = Person.fromEuSchema(input.nam),
                    dateOfBirthString = input.dob,
                    vaccinations = input.v?.let { it.map { v -> Vaccination.fromEuSchema(v) } },
                    recoveryStatements = input.r?.let { it.map { r -> RecoveryStatement.fromEuSchema(r) } },
                    tests = input.t?.let { it.map { t -> Test.fromEuSchema(t) } },
                )
            } catch (e: Throwable) {
                null
            }
        }
    }
}
