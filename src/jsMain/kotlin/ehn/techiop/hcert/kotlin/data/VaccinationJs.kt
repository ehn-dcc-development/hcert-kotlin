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
data class VaccinationJs(
    @SerialName("tg")
    val target: ValueSetEntryAdapter,

    @SerialName("vp")
    val vaccine: ValueSetEntryAdapter,

    @SerialName("mp")
    val medicinalProduct: ValueSetEntryAdapter,

    @SerialName("ma")
    val authorizationHolder: ValueSetEntryAdapter,

    @SerialName("dn")
    val doseNumber: Int,

    @SerialName("sd")
    val doseTotalNumber: Int,

    @SerialName("dt")
    @Serializable(with = JsDateSerializer::class)
    val date: Date,

    @SerialName("co")
    val country: String,

    @SerialName("is")
    val certificateIssuer: String,

    @SerialName("ci")
    val certificateIdentifier: String,
) {
    constructor(vaccination: Vaccination) : this(
        vaccination.target,
        vaccination.vaccine,
        vaccination.medicinalProduct,
        vaccination.authorizationHolder,
        vaccination.doseNumber,
        vaccination.doseTotalNumber,
        Date(vaccination.date.toString()),
        vaccination.country,
        vaccination.certificateIssuer,
        vaccination.certificateIdentifier
    )
}