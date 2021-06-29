package ehn.techiop.hcert.kotlin.chain

import ehn.techiop.hcert.kotlin.chain.DecodeResultJs.Companion.replaceDatesWithJsTypes
import ehn.techiop.hcert.kotlin.data.GreenCertificate
import ehn.techiop.hcert.kotlin.data.RecoveryStatement
import ehn.techiop.hcert.kotlin.data.Test
import ehn.techiop.hcert.kotlin.data.Vaccination
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
        extResult.chainDecodeResult.eudgc?.apply { replaceDatesWithJsTypes() },
    )

    internal companion object {

        private fun Any.toJsDate() = Date(toString())

        /**
         * we don't need custom deserializers, since the source is an object,
         * which already invoked them when parsing from the cbor source
         */
        @Suppress("USELESS_CAST")
        internal fun Any.replaceDatesWithJsTypes(propertyName: String) {
            val asDynamic = asDynamic()
            val timestamp = asDynamic[propertyName] as? Any? //not useless at all!
            (timestamp)?.let { asDynamic[propertyName] = it.toJsDate() }
        }

    }
}

private fun Test.replaceDatesWithJsTypes() {
    replaceDatesWithJsTypes("dateTimeResult")
    replaceDatesWithJsTypes("dateTimeSample")
}

private fun RecoveryStatement.replaceDatesWithJsTypes() {
    replaceDatesWithJsTypes("certificateValidFrom")
    replaceDatesWithJsTypes("certificateValidUntil")
    replaceDatesWithJsTypes("dateOfFirstPositiveTestResult")
}

private fun Vaccination.replaceDatesWithJsTypes() {
    replaceDatesWithJsTypes("date")
}

private fun GreenCertificate.replaceDatesWithJsTypes() {
    asDynamic()["dateOfBirth"] = try {
        Date(dateOfBirthString.substringBefore("T"))
    } catch (e: Throwable) {
        null
    }
    tests?.filterNotNull()?.forEach { it.replaceDatesWithJsTypes() }
    recoveryStatements?.filterNotNull()?.forEach { it.replaceDatesWithJsTypes() }
    vaccinations?.filterNotNull()?.forEach { it.replaceDatesWithJsTypes() }
}


