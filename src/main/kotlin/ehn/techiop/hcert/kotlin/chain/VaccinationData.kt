package ehn.techiop.hcert.kotlin.chain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VaccinationData(
    @SerialName("sub")
    val subject: Person? = null,
    @SerialName("vac")
    val vaccinations: List<Vaccination?>? = null,
    @SerialName("rec")
    val recoveryStatements: List<RecoveryStatement?>? = null,
    @SerialName("tst")
    val tests: List<Test?>? = null,
    @SerialName("cert")
    val metadata: DocumentMetadata? = null
)

@Serializable
data class Person(
    @SerialName("gn")
    val givenName: String? = null,
    @SerialName("fn")
    val familyName: String? = null,
    @SerialName("dob")
    val dateOfBirth: String? = null,
    @SerialName("gen")
    val gender: String? = null,
    @SerialName("id")
    val identifiers: List<Identifier?>? = null
)

@Serializable
data class Identifier(
    @SerialName("t")
    val type: String? = null,
    @SerialName("i")
    val id: String? = null
)

@Serializable
data class Vaccination(
    @SerialName("dis")
    val disease: String? = null,
    @SerialName("vap")
    val vaccine: String? = null,
    @SerialName("mep")
    val medicinalProduct: String? = null,
    @SerialName("aut")
    val authorizationHolder: String? = null,
    @SerialName("seq")
    val doseSequence: Int? = null,
    @SerialName("tot")
    val doseTotalNumber: Int? = null,
    @SerialName("lot")
    val lotNumber: String? = null,
    @SerialName("dat")
    val date: String? = null,
    @SerialName("adm")
    val administeringCentre: String? = null,
    @SerialName("cou")
    val country: String? = null
)


@Serializable
data class RecoveryStatement(
    @SerialName("dis")
    val disease: String? = null,
    @SerialName("dat")
    val date: String? = null,
    @SerialName("cou")
    val country: String? = null
)

@Serializable
data class DocumentMetadata(
    @SerialName("is")
    val issuer: String? = null,
    @SerialName("id")
    val identifier: String? = null,
    @SerialName("vf")
    val validFrom: String? = null,
    @SerialName("vu")
    val validUntil: String? = null,
    @SerialName("co")
    val country: String? = null,
    @SerialName("vr")
    val schemaVersion: String? = null,
    @SerialName("ty")
    val schemaType: String? = null
)

@Serializable
data class Test(
    @SerialName("dis")
    val disease: String? = null,
    @SerialName("typ")
    val type: String? = null,
    @SerialName("tna")
    val name: String? = null,
    @SerialName("tma")
    val manufacturer: String? = null,
    @SerialName("ori")
    val sampleOrigin: String? = null,
    @SerialName("dts")
    val dateTimeSample: String? = null,
    @SerialName("dtr")
    val dateTimeResult: String? = null,
    @SerialName("res")
    val result: String? = null,
    @SerialName("fac")
    val testFacility: String? = null,
    @SerialName("cou")
    val country: String? = null
)


