package ehn.techiop.hcert.kotlin.data

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

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

}

expect object ValueSetsInstanceHolder {
    val INSTANCE: ValueSetHolder
}

@Serializable
data class ValueSet @OptIn(ExperimentalTime::class) constructor(
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