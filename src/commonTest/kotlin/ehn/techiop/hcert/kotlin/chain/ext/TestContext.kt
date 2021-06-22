package ehn.techiop.hcert.kotlin.chain.ext


import ehn.techiop.hcert.kotlin.data.LenientInstantParser
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
    @Serializable(with = LenientInstantParser::class)
    val validationClock: Instant,

    @SerialName("DESCRIPTION")
    val description: String? = null,
)
