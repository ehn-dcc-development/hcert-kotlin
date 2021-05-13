package ehn.techiop.hcert.kotlin.trust

import kotlinx.datetime.Instant
import kotlinx.datetime.serializers.InstantIso8601Serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@ExperimentalSerializationApi
@Serializable
data class TrustList(
    @SerialName("f")
    @Serializable(with = InstantIso8601Serializer::class)
    val validFrom: Instant,

    @SerialName("u")
    @Serializable(with = InstantIso8601Serializer::class)
    val validUntil: Instant,

    @SerialName("c")
    val certificates: List<TrustedCertificate>
)