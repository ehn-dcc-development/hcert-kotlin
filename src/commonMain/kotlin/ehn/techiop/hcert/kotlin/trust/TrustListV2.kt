package ehn.techiop.hcert.kotlin.trust

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Contains a list of certificates, that will be loaded by clients to get a list of trusted certificates.
 * This structure does not contain a signature value, it will be provided in a separate file by [TrustListV2EncodeService].
 */
@Serializable
data class TrustListV2(

    @SerialName("c")
    val certificates: List<TrustedCertificateV2>

)
