package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.DecodeResultJs.Companion.replaceDateWithTimestap
import ehn.techiop.hcert.kotlin.chain.DecodeResultJs.Companion.replaceTimestampWithDate
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlin.js.Date

/**
 * This class contains "safe" objects for Javascript,
 * i.e. ones that can be serialized without mangled object structures and name mangling.
 * It also monkey-patches the GreenCertificate object to replace all kotlinx-datetime objects wih JS Date objects
 * One solution would be to annotate all exported classes with
 * [JsExport], but that won't work together with [Serializable]...
 */
@Serializable
data class DecodeResultJs(
    val isValid: Boolean,
    val error: String?,
    val metaInformation: VerificationResultJs,
    val greenCertificate: GreenCertificate?,
) {
    constructor(extResult: DecodeResult) : this(
        extResult.verificationResult.error == null,
        extResult.verificationResult.error?.name,
        VerificationResultJs(extResult.verificationResult),
        extResult.chainDecodeResult.eudgc?.apply { jsify() },
    )

    internal companion object {
        private fun Any.toJsDate() = Date(toString())

        private fun Date.toInstant() = Instant.parse(toISOString())

        private fun Date.toLocalDate() = LocalDate.parse(toISOString().substringBefore("T"))

        //we don't need custom deserializers, since the source is an object, which already invoked them when parsing from the cbor source
        internal fun Any.replaceTimestampWithDate(propertyName: String) {
            val asDynamic = asDynamic()

            @Suppress("USELESS_CAST")
            val timestamp = asDynamic[propertyName] as? Any? //not useless at all!
            (timestamp)?.let { asDynamic[propertyName] = it.toJsDate() }
        }

        internal fun Any.replaceDateWithTimestap(propertyName: String, isInstant: Boolean = false) {
            val asDynamic = asDynamic()
            val timestamp = asDynamic[propertyName] as? Date?
            (timestamp)?.let { asDynamic[propertyName] = if (isInstant) it.toInstant() else it.toLocalDate() }
        }
    }
}

fun GreenCertificate.jsify() {
    //replace DoB with plain JS Object
    asDynamic()["dateOfBirth"] = try {
        Date(dateOfBirthString.substringBefore("T"))
    } catch (e: Throwable) {
        null
    }

    //now replace instants
    tests?.filterNotNull()?.forEach {
        it.replaceTimestampWithDate("dateTimeResult")
        it.replaceTimestampWithDate("dateTimeSample")
    }
    recoveryStatements?.filterNotNull()?.forEach {
        it.replaceTimestampWithDate("certificateValidFrom")
        it.replaceTimestampWithDate("certificateValidUntil")
        it.replaceTimestampWithDate("dateOfFirstPositiveTestResult")
    }
    vaccinations?.filterNotNull()?.forEach {
        it.replaceTimestampWithDate("date")
    }
}

fun GreenCertificate.kotlinify() {
    //replace DoB with plain JS Object
    replaceDateWithTimestap("dateOfBirth")

    //now replace instants
    tests?.filterNotNull()?.forEach {
        it.replaceDateWithTimestap("dateTimeResult", isInstant = true)
        it.replaceDateWithTimestap("dateTimeSample", isInstant = true)
    }
    recoveryStatements?.filterNotNull()?.forEach {
        it.replaceDateWithTimestap("certificateValidFrom")
        it.replaceDateWithTimestap("certificateValidUntil")
        it.replaceDateWithTimestap("dateOfFirstPositiveTestResult")
    }
    vaccinations?.filterNotNull()?.forEach {
        it.replaceDateWithTimestap("date")
    }
}
