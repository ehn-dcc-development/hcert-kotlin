package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

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
) {

/*    fun toEuSchema() = PersonName().apply {
        fn = familyName
        fnt = familyNameTransliterated
        gn = givenName
        gnt = givenNameTransliterated
    }

    companion object {
        @JvmStatic
        fun fromEuSchema(it: PersonName) = Person(
            familyName = it.fn,
            familyNameTransliterated = it.fnt,
            givenName = it.gn,
            givenNameTransliterated = it.gnt,
        )
    }*/
}
