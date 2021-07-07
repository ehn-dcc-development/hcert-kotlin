package ehn.techiop.hcert.kotlin.data

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Test constructor(
    @SerialName("tg")
    val target: ValueSetEntryAdapter,

    @SerialName("tt")
    val type: ValueSetEntryAdapter,

    @SerialName("nm")
    val nameNaa: String? = null,

    @SerialName("ma")
    val nameRat: ValueSetEntryAdapter? = null,

    @SerialName("sc")
    @Serializable(with = LenientInstantParser::class)
    val dateTimeSample: Instant,

    @SerialName("dr")
    @Serializable(with = LenientNullableInstantParser::class)
    val dateTimeResult: Instant? = null,

    @SerialName("tr")
    val resultPositive: ValueSetEntryAdapter,

    @SerialName("tc")
    val testFacility: String? = null,

    @SerialName("co")
    val country: String,

    @SerialName("is")
    val certificateIssuer: String,

    @SerialName("ci")
    val certificateIdentifier: String,
)