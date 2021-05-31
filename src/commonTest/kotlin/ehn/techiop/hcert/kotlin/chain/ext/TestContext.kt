package ehn.techiop.hcert.kotlin.chain.ext


import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestContext(
    @SerialName("VERSION")
    val version: Int,

    @SerialName("SCHEMA")
    val schema: String,

    @SerialName("CERTIFICATE")
    val certificate: String?,

    @SerialName("VALIDATIONCLOCK")
    private val _validationClock: String,

    @SerialName("DESCRIPTION")
    val description: String? = null,
) {
    constructor(version: Int, schema: String, certificate: String?, validationClock: Instant, description: String?)
            : this(version, schema, certificate, validationClock.toString(), description)

    val validationClock = Instant.parse(fixInstantString(_validationClock))

    /**
     * Some memberstate tests from dgc-testdata actually don't include the Zulu time zone marker ...
     */
    private fun fixInstantString(s: String) =
        if (s.contains('Z') || s.contains("+")) s else s + 'Z'
}