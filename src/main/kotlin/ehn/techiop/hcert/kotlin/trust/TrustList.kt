package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.data.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class TrustList(
    @SerialName("f")
    @Serializable(with = InstantSerializer::class)
    val validFrom: Instant,

    @SerialName("u")
    @Serializable(with = InstantSerializer::class)
    val validUntil: Instant,

    @SerialName("c")
    val certificates: List<TrustedCertificate>
)