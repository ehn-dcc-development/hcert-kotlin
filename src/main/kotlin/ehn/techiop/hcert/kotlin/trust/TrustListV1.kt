package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.data.InstantLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class TrustListV1(
    @SerialName("f")
    @Serializable(with = InstantLongSerializer::class)
    val validFrom: Instant,

    @SerialName("u")
    @Serializable(with = InstantLongSerializer::class)
    val validUntil: Instant,

    @SerialName("c")
    val certificates: List<TrustedCertificateV1>
)