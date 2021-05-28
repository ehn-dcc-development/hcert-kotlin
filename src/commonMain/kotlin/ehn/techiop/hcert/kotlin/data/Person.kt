package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Person(
    @SerialName("fn")
    val familyName: String? = null,

    @SerialName("fnt")
    val familyNameTransliterated: String? = null,

    @SerialName("gn")
    val givenName: String,

    @SerialName("gnt")
    val givenNameTransliterated: String? = null,
)
