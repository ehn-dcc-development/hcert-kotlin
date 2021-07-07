package ehn.techiop.hcert.kotlin.rules

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Contains a list of business rules, see [BusinessRule]
 */
@Serializable
data class BusinessRulesContainer(

    @SerialName("r")
    val rules: List<BusinessRule>

)
