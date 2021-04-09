package ehn.techiop.hcert.kotlin.chain

import kotlinx.serialization.Serializable

@Serializable
data class VaccinationData(
    val sub: Person? = null,
    val vac: List<Vaccination?>? = null,
    val rec: PastInfection? = null,
    val tst: Test? = null,
    val certificateMetadata: CertificateMetadata? = null
)

@Serializable
data class Person(
    val n: String? = null,
    val dob: String? = null,
    val id: List<Identifier?>? = null
)

@Serializable
data class Identifier(
    val t: String? = null,
    val i: String? = null
)

@Serializable
data class Vaccination(
    val dis: String? = null,
    val des: String? = null,
    val nam: String? = null,
    val aut: String? = null,
    val seq: Int? = null,
    val tot: Int? = null,
    val lot: String? = null,
    val dat: String? = null,
    val adm: String? = null,
    val cou: String? = null
)


@Serializable
data class PastInfection(
    val dis: String? = null,
    val dat: String? = null,
    val cou: String? = null
)

@Serializable
data class CertificateMetadata(
    val issuer: String? = null,
    val identifier: String? = null,
    val validFrom: String? = null,
    val validUntil: String? = null,
    val validUntilextended: String? = null,
    val revokelistidentifier: String? = null,
    val schemaVersion: String? = null
)

@Serializable
data class Test(
    val dis: String? = null,
    val typ: String? = null,
    val tna: String? = null,
    val tma: String? = null,
    val ori: String? = null,
    val dat: String? = null,
    val res: String? = null,
    val fac: String? = null,
    val cou: String? = null
)


