package ehn.techiop.hcert.kotlin.data

import MainResourceHolder
import R
import ehn.techiop.hcert.kotlin.chain.fromBase64
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun R.load(input: String): ByteArray? {
    val value = get(if (input.startsWith("/")) input.substring(1); else input)
    return value?.fromBase64()
}

fun R.loadAsString(input: String) = load(input)?.decodeToString()

actual object ValueSetsInstanceHolder {
    actual val INSTANCE: ValueSetHolder = run {
        val values = mutableListOf<ValueSet>()
        inputPaths.forEach { input ->
            MainResourceHolder.loadAsString(input)?.let { values.add(Json.decodeFromString(it)) }
        }
        ValueSetHolder(values)
    }
}
