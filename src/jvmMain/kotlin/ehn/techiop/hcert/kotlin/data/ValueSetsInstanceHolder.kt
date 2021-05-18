package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

actual object ValueSetsInstanceHolder {
    actual val INSTANCE: ValueSetHolder by lazy {
        val inputPaths = listOf(
            "src/commonMain/resources/value-sets/disease-agent-targeted.json",
            "src/commonMain/resources/value-sets/test-manf.json",
            "src/commonMain/resources/value-sets/test-result.json",
            "src/commonMain/resources/value-sets/vaccine-mah-manf.json",
            "src/commonMain/resources/value-sets/vaccine-medicinal-product.json",
            "src/commonMain/resources/value-sets/vaccine-prophylaxis.json",
        )
        ValueSetHolder(inputPaths.map { Json.decodeFromString(File(it).readText()) })
    }
}