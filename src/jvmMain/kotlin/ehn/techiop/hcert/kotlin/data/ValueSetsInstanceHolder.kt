package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

actual object ValueSetsInstanceHolder {

    actual val INSTANCE: ValueSetHolder by lazy {
        ValueSetHolder(inputPaths.map { "src/commonMain/resources/$it" }
            .map { Json.decodeFromString(File(it).readText()) })
    }

}