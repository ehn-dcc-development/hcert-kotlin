package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.Date

/**
 * This class contains "safe" objects for Javascript,
 * i.e. ones that can be serialized without name mangling.
 * One solution would be to annotate all exported classes with
 * [JsExport], but that won't work together with [Serializable]...
 */
@Serializable
data class TestJs constructor(
    @SerialName("tg")
    val target: ValueSetEntryAdapter,

    @SerialName("tt")
    val type: ValueSetEntryAdapter,

    @SerialName("nm")
    val nameNaa: String? = null,

    @SerialName("ma")
    val nameRat: ValueSetEntryAdapter? = null,

    @SerialName("sc")
    @Serializable(with = JsDateInstantSerializer::class)
    val dateTimeSample: Date,

    @SerialName("dr")
    @Serializable(with = JsDateInstantSerializer::class)
    val dateTimeResult: Date? = null,

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
    constructor(test: Test) : this(
        test.target,
        test.type,
        test.nameNaa,
        test.nameRat,
        Date(test.dateTimeSample.toString()),
        test.dateTimeResult?.let { Date(it.toString()) },
        test.resultPositive,
        test.testFacility,
        test.country,
        test.certificateIssuer,
        test.certificateIdentifier
    )
}