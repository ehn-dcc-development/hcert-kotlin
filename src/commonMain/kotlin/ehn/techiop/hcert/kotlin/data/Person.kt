package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsName

@Serializable
data class Person(
    @SerialName("fn")
    @JsName("familyName")
    val familyName: String? = null,

    @SerialName("fnt")
    @JsName("familyNameTransliterated")
    val familyNameTransliterated: String,

    @SerialName("gn")
    @JsName("givenName")
    val givenName: String? = null,

    @SerialName("gnt")
    @JsName("givenNameTransliterated")
    val givenNameTransliterated: String? = null,
)
