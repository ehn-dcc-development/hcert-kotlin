package ehn.techiop.hcert.kotlin.data

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

actual object ValueSetsInstanceHolder {

    actual val INSTANCE: ValueSetHolder by lazy {
        ValueSetHolder(inputPaths
            .mapNotNull { this::class.java.getResource(it) }
            .map { it.readText() }
            .map { Json.decodeFromString(it) }
        )
    }

}