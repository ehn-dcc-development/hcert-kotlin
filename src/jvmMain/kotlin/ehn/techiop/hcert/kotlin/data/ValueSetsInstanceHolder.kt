package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

actual object ValueSetsInstanceHolder {
    actual val INSTANCE: ValueSetHolder by lazy {
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