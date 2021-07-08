package ehn.techiop.hcert.kotlin.valueset

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Contains a list of value sets, ee [ValueSet]
 */
@Serializable
data class ValueSetContainer(

    @SerialName("v")
    val valueSets: Array<ValueSet>

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ValueSetContainer

        if (!valueSets.contentEquals(other.valueSets)) return false

        return true
    }

    override fun hashCode(): Int {
        return valueSets.contentHashCode()
    }
}
