package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.data.DigitalGreenCertificate
import ehn.techiop.hcert.data.Id
import ehn.techiop.hcert.data.Rec
import ehn.techiop.hcert.data.Tst
import ehn.techiop.hcert.data.Vac
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Data {
    companion object {
        @JvmStatic
        fun fromSchema(input: DigitalGreenCertificate) = GreenCertificate(
            subject = input.sub.let {
                Person(
                    givenName = it.gn,
                    givenNameTransliterated = it.gnt,
                    familyName = it.fn,
                    familyNameTransliterated = it.fnt,
                    dateOfBirth = parseLocalDate(it.dob),
                    gender = it.gen?.value()?.let { Gender.findByValue(it) },
                    identifiers = it.id?.let { createIdentifiers(it) }
                )
            },
            vaccinations = input.vac?.let { createVaccinations(it) },
            tests = input.tst?.let { createTestResult(it) },
            recoveryStatements = input.rec?.let { createRecovery(it) },
            schemaVersion = input.v,
            identifier = input.dgcid
        )

        private fun createIdentifiers(list: List<Id>) = list.map {
            Identifier(
                type = it.t.value().let { type -> IdentifierType.findByValue(type) },
                id = it.i,
                country = it.c
            )
        }

        private fun createVaccinations(list: List<Vac>) = list.map {
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

        private fun createTestResult(list: List<Tst>) = list.map {
            Test(
                disease = it.dis,
                type = it.typ,
                manufacturer = it.tma,
                sampleOrigin = it.ori,
                dateTimeSample = parseLocalDateTime(it.dts),
                dateTimeResult = parseLocalDateTime(it.dtr),
                resultPositive = parseTestResult(it.res),
                testFacility = it.fac,
                country = it.cou
            )
        }

        private fun createRecovery(list: List<Rec>) = list.map {
            RecoveryStatement(
                disease = it.dis,
                date = parseLocalDate(it.dat),
                country = it.cou
            )
        }

        private fun parseLocalDate(input: String?) = LocalDate.parse(input, DateTimeFormatter.ISO_DATE)

        private fun parseLocalDateTime(input: String) = LocalDateTime.parse(input, DateTimeFormatter.ISO_DATE_TIME)

        private fun parseLocalDateTime(input: Int) = LocalDateTime.from(Instant.ofEpochSecond(input.toLong()))

        private fun parseTestResult(input: String) = input == "1240591000000104"

    }

}

@Serializable
data class GreenCertificate(
    @SerialName("v")
    val schemaVersion: String,

    @SerialName("dgcid")
    val identifier: String,

    @SerialName("sub")
    val subject: Person,

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
    val givenName: String,

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
    val dateOfBirth: LocalDate,

    @SerialName("gen")
    val gender: Gender? = null,
)

@Serializable
enum class Gender(val value: String) {
    @SerialName("male")
    MALE("male"),

    @SerialName("female")
    FEMALE("female"),

    @SerialName("other")
    OTHER("other"),

    @SerialName("unknown")
    UNKNOWN("unknown");

    companion object {
        fun findByValue(value: String): Gender {
            return values().firstOrNull { it.value == value } ?: UNKNOWN
        }
    }
}

@Serializable
data class Identifier(
    @SerialName("t")
    val type: IdentifierType,

    @SerialName("i")
    val id: String,

    @SerialName("c")
    val country: String? = null
)

@Serializable
enum class IdentifierType(val value: String) {
    @SerialName("PP")
    PASSPORT("PP"),

    @SerialName("NN")
    NATIONAL_IDENTIFIER("NN"),

    @SerialName("CZ")
    CITIZENSHIP("CZ"),

    @SerialName("HC")
    HEALTH("HC"),

    @SerialName("NI")
    NATIONAL_UNIQUE_INDIVIDUAL("NI"),

    @SerialName("MB")
    MEMBER("MB"),

    @SerialName("NH")
    NATIONAL_HEALTH("NH");

    companion object {
        fun findByValue(value: String): IdentifierType {
            return values().firstOrNull { it.value == value } ?: NATIONAL_IDENTIFIER
        }
    }
}

@Serializable
data class Vaccination(
    @SerialName("dis")
    val disease: String,

    @SerialName("vap")
    val vaccine: String,

    @SerialName("mep")
    val medicinalProduct: String,

    @SerialName("aut")
    val authorizationHolder: String,

    @SerialName("seq")
    val doseSequence: Int,

    @SerialName("tot")
    val doseTotalNumber: Int,

    @SerialName("lot")
    val lotNumber: String? = null,

    @SerialName("dat")
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,

    @SerialName("adm")
    val administeringCentre: String? = null,

    @SerialName("cou")
    val country: String
)


@Serializable
data class RecoveryStatement(
    @SerialName("dis")
    val disease: String,

    @SerialName("dat")
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,

    @SerialName("cou")
    val country: String
)

@Serializable
data class Test(
    @SerialName("dis")
    val disease: String,

    @SerialName("typ")
    val type: String,

    @SerialName("tma")
    val manufacturer: String? = null,

    @SerialName("ori")
    val sampleOrigin: String? = null,

    @SerialName("dts")
    @Serializable(with = LocalDateTimeSerializer::class)
    val dateTimeSample: LocalDateTime,

    @SerialName("dtr")
    @Serializable(with = LocalDateTimeSerializer::class)
    val dateTimeResult: LocalDateTime,

    @SerialName("res")
    @Serializable(with = TestResultSerializer::class)
    val resultPositive: Boolean,

    @SerialName("fac")
    val testFacility: String,

    @SerialName("cou")
    val country: String
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

@Serializer(forClass = LocalDateTime::class)
object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.from(Instant.ofEpochSecond(decoder.decodeLong()))
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeLong(value.atZone(ZoneId.systemDefault()).toInstant().epochSecond)
    }
}

/**
 * Values from hcert-schema, according to SNOMED CT
 */
@Serializer(forClass = Boolean::class)
object TestResultSerializer : KSerializer<Boolean> {
    override fun deserialize(decoder: Decoder): Boolean {
        return when (decoder.decodeString()) {
            "1240591000000104" -> true
            else -> false
        }
    }

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeString(if (value) "1240591000000104" else "1240591000000102")
    }
}

