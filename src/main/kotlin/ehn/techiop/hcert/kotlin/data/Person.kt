package ehn.techiop.hcert.kotlin.data

import ehn.techiop.hcert.data.PersonName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Person(
    @SerialName("gn")
    val givenName: String,

    @SerialName("gnt")
    val givenNameTransliterated: String? = null,

    @SerialName("fn")
    val familyName: String? = null,

    @SerialName("fnt")
    val familyNameTransliterated: String? = null,
) {
    companion object {
        @JvmStatic
        fun fromEuSchema(it: PersonName) = Person(
            givenName = it.gn,
            givenNameTransliterated = it.gnt,
            familyName = it.fn,
            familyNameTransliterated = it.fnt,
        )
    }
}
