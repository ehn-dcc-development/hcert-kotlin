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
    //@Serializable(with = X509CertificateSerializer::class)
    val certificate: String?,

    @SerialName("VALIDATIONCLOCK")
    //@Serializable(with = InstantStringSerializer::class)
    val validationClock: Instant?,

    @SerialName("DESCRIPTION")
    val description: String,
)