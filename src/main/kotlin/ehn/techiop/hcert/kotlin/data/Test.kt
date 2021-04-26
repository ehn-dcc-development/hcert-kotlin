package ehn.techiop.hcert.kotlin.data

import ehn.techiop.hcert.data.TestEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Serializable
data class Test(
    @SerialName("tg")
    val target: DiseaseTargetType,

    @SerialName("tt")
    val type: String,

    @SerialName("nm")
    val nameNaa: String? = null,

    @SerialName("ma")
    val nameRat: String? = null, // may be an enum

    @SerialName("sc")
    @Serializable(with = IsoOffsetDateTimeSerializer::class)
    val dateTimeSample: OffsetDateTime,

    @SerialName("dr")
    @Serializable(with = IsoOffsetDateTimeSerializer::class)
    val dateTimeResult: OffsetDateTime? = null,

    @SerialName("tr")
    @Serializable(with = TestResultSerializer::class)
    val resultPositive: Boolean,

    @SerialName("tc")
    val testFacility: String,

    @SerialName("co")
    val country: String,

    @SerialName("is")
    val certificateIssuer: String,

    @SerialName("ci")
    val certificateIdentifier: String,
) {
    companion object {
        @JvmStatic
        fun fromEuSchema(it: TestEntry) = Test(
            target = DiseaseTargetType.findByValue(it.tg.value()),
            type = it.tt,
            nameNaa = it.nm,
            nameRat = it.ma?.value(),
            dateTimeSample = it.sc.toInstant().atOffset(ZoneOffset.UTC),
            dateTimeResult = it.dr?.toInstant()?.atOffset(ZoneOffset.UTC),
            resultPositive = it.tr.value() == "260373001",
            testFacility = it.tc,
            country = it.co,
            certificateIssuer = it.`is`,
            certificateIdentifier = it.ci
        )
    }
}