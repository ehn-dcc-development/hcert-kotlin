package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.LocalDate

/**
 * Holds a list of value sets, that get lazily loaded from the JSON files in "src/main/resources/value-sets".
 */
data class ValueSetHolder(
    val valueSets: List<ValueSet>
) {
    fun find(valueSetId: String, key: String): ValueSetEntryAdapter {
        return valueSets.firstOrNull { it.valueSetId == valueSetId }?.valueSetValues?.get(key)?.let {
            ValueSetEntryAdapter(key, it)
        } ?: ValueSetEntryAdapter(key, ValueSetEntry.UNKNOWN)
    }

    fun find(key: String): ValueSetEntryAdapter {
        valueSets.forEach {
            if (it.valueSetValues.containsKey(key))
                return ValueSetEntryAdapter(key, it.valueSetValues[key]!!)
        }
        return ValueSetEntryAdapter(key, ValueSetEntry.UNKNOWN)
    }

    companion object {
        val INSTANCE: ValueSetHolder by lazy {
            val inputPaths = listOf(
                "/value-sets/disease-agent-targeted.json",
                "/value-sets/test-manf.json",
                "/value-sets/test-result.json",
                "/value-sets/vaccine-mah-manf.json",
                "/value-sets/vaccine-medicinal-product.json",
                "/value-sets/vaccine-prophylaxis.json",
            )
            ValueSetHolder(inputPaths.map { Json.decodeFromString(this::class.java.getResource(it).readText()) })
        }
    }
}

@Serializable
data class ValueSet(
    val valueSetId: String,
    @Serializable(with = LocalDateSerializer::class)
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
) {
    companion object {
        val UNKNOWN = ValueSetEntry("", "", false, "", "", null)
    }
}

@Serializable(with = ValueSetEntryAdapterSerializer::class)
data class ValueSetEntryAdapter(
    val key: String,
    val valueSetEntry: ValueSetEntry
)