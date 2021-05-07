package ehn.techiop.hcert.kotlin.data

import ehn.techiop.hcert.data.TestEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date

@Serializable
data class Test(
    @SerialName("tg")
    val target: ValueSetEntryAdapter,

    @SerialName("tt")
    val type: String,

    @SerialName("nm")
    val nameNaa: String? = null,

    @SerialName("ma")
    val nameRat: ValueSetEntryAdapter? = null,

    @SerialName("sc")
    @Serializable(with = IsoOffsetDateTimeSerializer::class)
    val dateTimeSample: OffsetDateTime,

    @SerialName("dr")
    @Serializable(with = IsoOffsetDateTimeSerializer::class)
    val dateTimeResult: OffsetDateTime? = null,

    @SerialName("tr")
    val resultPositive: ValueSetEntryAdapter,

    @SerialName("tc")
    val testFacility: String,

    @SerialName("co")
    val country: String,

    @SerialName("is")
    val certificateIssuer: String,

    @SerialName("ci")
    val certificateIdentifier: String,
) {
    fun toEuSchema() = TestEntry().apply {
        tg = target.key
        tt = type
        nm = nameNaa
        ma = nameRat?.key
        sc = Date(dateTimeSample.toInstant().toEpochMilli())
        dr = dateTimeResult?.let { Date(it.toInstant().toEpochMilli()) }
        tr = resultPositive.key
        tc = testFacility
        co = country
        `is` = certificateIssuer
        ci = certificateIdentifier
    }

    companion object {
        @JvmStatic
        fun fromEuSchema(it: TestEntry) = Test(
            target = ValueSetHolder.INSTANCE.find("disease-agent-targeted", it.tg),
            type = it.tt,
            nameNaa = it.nm,
            nameRat = it.ma?.let { ValueSetHolder.INSTANCE.find("covid-19-lab-test-manufacturer-and-name", it) },
            dateTimeSample = it.sc.toInstant().atOffset(ZoneOffset.UTC),
            dateTimeResult = it.dr?.toInstant()?.atOffset(ZoneOffset.UTC),
            resultPositive = ValueSetHolder.INSTANCE.find("covid-19-lab-result", it.tr),
            testFacility = it.tc,
            country = it.co,
            certificateIssuer = it.`is`,
            certificateIdentifier = it.ci
        )
    }
}