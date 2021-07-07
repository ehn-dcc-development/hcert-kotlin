package ehn.techiop.hcert.kotlin.valueset

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Contains a list of value sets, ee [ValueSet]
 */
@Serializable
data class ValueSetContainer(

    @SerialName("v")
    val valueSets: List<ValueSet>

)
