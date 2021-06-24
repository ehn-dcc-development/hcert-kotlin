package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.js.Date

/**
 * This class contains "safe" objects for Javascript,
 * i.e. ones that can be serialized without name mangling.
 * One solution would be to annotate all exported classes with
 * [JsExport], but that won't work together with [Serializable]...
 */
@Serializable
data class GreenCertificateJs(
    @SerialName("ver")
    val schemaVersion: String,

    @SerialName("nam")
    val subject: Person,

    @SerialName("dob")
    val dateOfBirthString: String,

    @SerialName("v")
    val vaccinations: Array<VaccinationJs?>? = null,

    @SerialName("r")
    val recoveryStatements: Array<RecoveryStatementJs?>? = null,

    @SerialName("t")
    val tests: Array<TestJs?>? = null,
) {
    constructor(greenCertificate: GreenCertificate) : this(
        greenCertificate.schemaVersion,
        greenCertificate.subject,
        greenCertificate.dateOfBirthString,
        greenCertificate.vaccinations?.filterNotNull()?.map { VaccinationJs(it) }?.toTypedArray(),
        greenCertificate.recoveryStatements?.filterNotNull()?.map { RecoveryStatementJs(it) }?.toTypedArray(),
        greenCertificate.tests?.filterNotNull()?.map { TestJs(it) }?.toTypedArray()
    )

    /**
     * For [dateOfBirthString] ("dob"), month and day are optional in eu-dcc-schema 1.2.1,
     * so we may not be able to get a valid [Date] from it.
     * Be lenient, i.e. strip a timestamp, if it is included
     */
    @Transient
    val dateOfBirth: Date? = try {
        Date(dateOfBirthString.substringBefore("T"))
    } catch (e: Throwable) {
        null
    }


}
