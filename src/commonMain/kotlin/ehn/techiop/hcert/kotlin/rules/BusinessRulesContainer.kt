package ehn.techiop.hcert.kotlin.rules

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Contains a list of business rules, see [BusinessRule]
 */
@Serializable
data class BusinessRulesContainer(

    @SerialName("r")
    val rules: Array<BusinessRule>

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BusinessRulesContainer

        if (!rules.contentEquals(other.rules)) return false

        return true
    }

    override fun hashCode(): Int {
        return rules.contentHashCode()
    }
}
