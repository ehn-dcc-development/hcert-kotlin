package ehn.techiop.hcert.kotlin.trust

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class KeyType {
    @SerialName("r")
    RSA,

    @SerialName("e")
    EC
}