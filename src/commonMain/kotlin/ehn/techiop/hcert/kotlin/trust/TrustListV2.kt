package ehn.techiop.hcert.kotlin.trust

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@ExperimentalSerializationApi

@Serializable
data class TrustListV2(

    @SerialName("c")
    val certificates: List<TrustedCertificateV2>

)