package ehn.techiop.hcert.kotlin.valueset

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One specific value set, as raw JSON
 */
@Serializable
data class ValueSet(

    @SerialName("n")
    val name: String,

    @SerialName("v")
    val valueSet: String

)