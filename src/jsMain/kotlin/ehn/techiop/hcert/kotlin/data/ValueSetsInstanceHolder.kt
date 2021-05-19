package ehn.techiop.hcert.kotlin.data

import ResourceHolder
import ehn.techiop.hcert.kotlin.chain.fromBase64
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

actual object ValueSetsInstanceHolder {
    actual val INSTANCE: ValueSetHolder = run {
        val values = mutableListOf<ValueSet>()

        inputPaths.forEach { input ->
            val get = ResourceHolder.get(input)
            //println(get)
            val fromBase64 = get?.fromBase64()
            //println(fromBase64)
            val decodeToString = fromBase64?.decodeToString()
            //println(decodeToString)
            decodeToString?.let { values.add(Json.decodeFromString(it)) }
        }


        ValueSetHolder(values)
    }
}