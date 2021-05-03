package ehn.techiop.hcert.kotlin.chain.ext

import ehn.techiop.hcert.kotlin.data.IsoOffsetDateTimeSerializer
import ehn.techiop.hcert.kotlin.data.X509CertificateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.security.cert.X509Certificate
import java.time.OffsetDateTime

@Serializable
data class TestContext(
    @SerialName("VERSION")
    val version: Int,
    @SerialName("SCHEMA")
    val schema: String,
    @SerialName("CERTIFICATE")
    @Serializable(with = X509CertificateSerializer::class)
    val certificate: X509Certificate,
    @SerialName("VALIDATIONCLOCK")
    @Serializable(with = IsoOffsetDateTimeSerializer::class)
    val validationClock: OffsetDateTime,
    @SerialName("DESCRIPTION")
    val description: String,
)