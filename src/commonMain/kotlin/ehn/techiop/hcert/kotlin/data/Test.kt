package ehn.techiop.hcert.kotlin.data

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsName

@Serializable
data class Test constructor(
    @SerialName("tg")
    @JsName("target")
    val target: ValueSetEntryAdapter,

    @SerialName("tt")
    @JsName("type")
    val type: ValueSetEntryAdapter,

    @SerialName("nm")
    @JsName("nameNaa")
    val nameNaa: String? = null,

    @SerialName("ma")
    @JsName("nameRat")
    val nameRat: ValueSetEntryAdapter? = null,

    @SerialName("sc")
    @Serializable(with = LenientInstantParser::class)
    @JsName("dateTimeSample")
    val dateTimeSample: Instant,

    @SerialName("dr")
    @Serializable(with = LenientInstantParser::class)
    @JsName("dateTimeResult")
    val dateTimeResult: Instant? = null,

    @SerialName("tr")
    @JsName("resultPositive")
    val resultPositive: ValueSetEntryAdapter,

    @SerialName("tc")
    @JsName("testFacility")
    val testFacility: String,

    @SerialName("co")
    @JsName("country")
    val country: String,

    @SerialName("is")
    @JsName("certificateIssuer")
    val certificateIssuer: String,

    @SerialName("ci")
    @JsName("certificateIdentifier")
    val certificateIdentifier: String,
)