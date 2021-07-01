package ehn.techiop.hcert.kotlin.trust

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * One specific business rule, as raw JSON
 */
@Serializable
data class BusinessRule(
    @SerialName("r")
    val rule: String
)