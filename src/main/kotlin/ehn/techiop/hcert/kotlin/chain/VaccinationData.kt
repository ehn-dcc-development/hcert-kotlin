package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.data.DigitalGreenCertificate
import ehn.techiop.hcert.data.Id
import ehn.techiop.hcert.data.Rec
import ehn.techiop.hcert.data.Tst
import ehn.techiop.hcert.data.Vac
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Data {
    companion object {
        @JvmStatic
        fun fromSchema(input: DigitalGreenCertificate) = GreenCertificate(
            subject = input.sub?.let {
                Person(
                    givenName = it.gn,
                    givenNameTransliterated = it.gnt,
                    familyName = it.fn,
                    familyNameTransliterated = it.fnt,
                    dateOfBirth = parseLocalDate(it.dob),
                    gender = it.gen?.value(),
                    identifiers = it.id?.let { createIdentifiers(it) }
                )
            },
            vaccinations = input.vac?.let { createVaccinations(it) },
            tests = input.tst?.let { createTestResult(it) },
            recoveryStatements = input.rec?.let { createRecovery(it) },
            schemaVersion = input.v,
            identifier = input.dgcid
        )

        private fun createIdentifiers(list: List<Id>) = list.filterNotNull().map {
            Identifier(
                type = it.t?.value(),
                id = it.i,
                country = it.c
            )
        }

        private fun createVaccinations(list: List<Vac>) = list.filterNotNull().map {
            Vaccination(
                disease = it.dis,
                vaccine = it.vap,
                medicinalProduct = it.mep,
                authorizationHolder = it.aut,
                doseSequence = it.seq,
                doseTotalNumber = it.tot,
                date = parseLocalDate(it.dat),
                country = it.cou,
                lotNumber = it.lot,
                administeringCentre = it.adm
            )
        }

        private fun createTestResult(list: List<Tst>) = list.filterNotNull().map {
            Test(
                disease = it.dis,
                type = it.typ,
                manufacturer = it.tma,
                sampleOrigin = it.ori,
                dateTimeSample = parseLocalDateTime(it.dts),
                dateTimeResult = parseLocalDateTime(it.dtr),
                result = it.res,
                testFacility = it.fac,
                country = it.cou
            )
        }

        private fun createRecovery(list: List<Rec>) = list.filterNotNull().map {
            RecoveryStatement(
                disease = it.dis,
                date = parseLocalDate(it.dat),
                country = it.cou
            )
        }

        private fun parseLocalDate(input: String?) =
            input?.let { LocalDate.parse(input, DateTimeFormatter.ISO_DATE) }

        private fun parseLocalDateTime(input: String?) =
            input?.let { LocalDateTime.parse(input, DateTimeFormatter.ISO_DATE_TIME) }

        private fun parseLocalDateTime(input: Int?) =
            input?.let { LocalDateTime.from(Instant.ofEpochSecond(it.toLong())) }

    }

}

@Serializable
data class GreenCertificate(
    @SerialName("v")
    val schemaVersion: String? = null,
    @SerialName("dgcid")
    val identifier: String? = null,
    @SerialName("sub")
    val subject: Person? = null,
    @SerialName("vac")
    val vaccinations: List<Vaccination?>? = listOf(),
    @SerialName("rec")
    val recoveryStatements: List<RecoveryStatement?>? = listOf(),
    @SerialName("tst")
    val tests: List<Test?>? = listOf(),
)

@Serializable
data class Person(
    @SerialName("gn")
    val givenName: String? = null,
    @SerialName("gnt")
    val givenNameTransliterated: String? = null,
    @SerialName("fn")
    val familyName: String? = null,
    @SerialName("fnt")
    val familyNameTransliterated: String? = null,
    @SerialName("id")
    val identifiers: List<Identifier?>? = null,
    @SerialName("dob")
    @Serializable(with = LocalDateSerializer::class)
    val dateOfBirth: LocalDate? = null,
    @SerialName("gen")
    val gender: String? = null,
)

@Serializable
data class Identifier(
    @SerialName("t")
    val type: String? = null,
    @SerialName("i")
    val id: String? = null,
    @SerialName("c")
    val country: String? = null
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
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate? = null,
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
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate? = null,
    @SerialName("cou")
    val country: String? = null
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
    @Contextual
    val dateTimeSample: LocalDateTime? = null,
    @SerialName("dtr")
    @Contextual
    val dateTimeResult: LocalDateTime? = null,
    @SerialName("res")
    val result: String? = null,
    @SerialName("fac")
    val testFacility: String? = null,
    @SerialName("cou")
    val country: String? = null
)


@Serializer(forClass = LocalDate::class)
object LocalDateSerializer : KSerializer<LocalDate> {
    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), DateTimeFormatter.ISO_DATE)
    }

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(DateTimeFormatter.ISO_DATE))
    }
}

