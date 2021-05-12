package ehn.techiop.hcert.kotlin.trust

import ehn.techiop.hcert.kotlin.data.InstantLongSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@ExperimentalSerializationApi
@Serializable
data class TrustList(
    @SerialName("f")
    @Serializable(with = InstantLongSerializer::class)
    val validFrom: Instant,

    @SerialName("u")
    @Serializable(with = InstantLongSerializer::class)
    val validUntil: Instant,

    @SerialName("c")
    val certificates: List<TrustedCertificate>
)