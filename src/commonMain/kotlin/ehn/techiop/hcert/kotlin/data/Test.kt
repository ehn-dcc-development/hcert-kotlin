package ehn.techiop.hcert.kotlin.data

import kotlinx.datetime.Instant
import kotlinx.datetime.serializers.InstantIso8601Serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@ExperimentalSerializationApi
data class Test constructor(
    @SerialName("tg")
    val target: ValueSetEntryAdapter,

    @SerialName("tt")
    val type: String,

    @SerialName("nm")
    val nameNaa: String? = null,

    @SerialName("ma")
    val nameRat: ValueSetEntryAdapter? = null,

    @SerialName("sc")
    @Serializable(with = InstantIso8601Serializer::class)
    val dateTimeSample: Instant,

    @SerialName("dr")
    @Serializable(with = InstantIso8601Serializer::class)
    val dateTimeResult: Instant? = null,

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
    /*
    val dateTimeSample by lazy {
        Instant.fromEpochSeconds(sc)
    }
    val dateTimeResult by lazy {
        dr?.let { Instant.fromEpochSeconds(it) }
    }*/
    /*fun toEuSchema() = TestEntry().apply {
        tg = target.key
        tt = type
        nm = nameNaa
        ma = nameRat?.key
        sc = Date(dateTimeSample.toEpochMilli())
        dr = dateTimeResult?.let { Date(it.toEpochMilli()) }
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
            dateTimeSample = it.sc.toInstant(),
            dateTimeResult = it.dr?.toInstant(),
            resultPositive = ValueSetHolder.INSTANCE.find("covid-19-lab-result", it.tr),
            testFacility = it.tc,
            country = it.co,
            certificateIssuer = it.`is`,
            certificateIdentifier = it.ci
        )
    }*/
}