package ehn.techiop.hcert.kotlin.trust

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class TrustListV2(

    @SerialName("c")
    val certificates: List<TrustedCertificateV2>

)