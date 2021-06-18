package ehn.techiop.hcert.kotlin.data

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

val inputPaths = listOf(
    "/value-sets/disease-agent-targeted.json",
    "/value-sets/test-manf.json",
    "/value-sets/test-type.json",
    "/value-sets/test-result.json",
    "/value-sets/vaccine-mah-manf.json",
    "/value-sets/vaccine-medicinal-product.json",
    "/value-sets/vaccine-prophylaxis.json",
  //  "/value-sets/country-2-codes.json",
)

/**
 * Holds a list of value sets, that get lazily loaded from the JSON files in "src/main/resources/value-sets".
 */
data class ValueSetHolder(
    val valueSets: List<ValueSet>
) {
    fun find(valueSetId: String, key: String): ValueSetEntryAdapter {
        return valueSets.firstOrNull { it.valueSetId == valueSetId }
            ?.valueSetValues?.get(key)?.let { ValueSetEntryAdapter(key, it) }
            ?: return buildMissingValueSetEntry(key, valueSetId)
    }

    fun find(key: String): ValueSetEntryAdapter {
        valueSets.forEach {
            if (it.valueSetValues.containsKey(key))
                return ValueSetEntryAdapter(key, it.valueSetValues[key]!!)
        }
        return buildMissingValueSetEntry(key, null)
    }

    private fun buildMissingValueSetEntry(key: String, valueSetId: String?): ValueSetEntryAdapter {
        val entry = ValueSetEntry(key, "en", false, "http://example.com/missing", "0", valueSetId)
        return ValueSetEntryAdapter(key, entry)
    }

}

expect object ValueSetsInstanceHolder {
    val INSTANCE: ValueSetHolder
}

@Serializable
data class ValueSet constructor(
    val valueSetId: String,
    val valueSetDate: LocalDate,
    val valueSetValues: Map<String, ValueSetEntry>
)

@Serializable
data class ValueSetEntry(
    val display: String,
    val lang: String,
    val active: Boolean,
    val system: String,
    val version: String,
    val valueSetId: String? = null
)

@Serializable(with = ValueSetEntryAdapterSerializer::class)
data class ValueSetEntryAdapter(
    val key: String,
    val valueSetEntry: ValueSetEntry
)
