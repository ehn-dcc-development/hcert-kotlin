package ehn.techiop.hcert.kotlin.log

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.Napier
import kotlin.math.floor
import kotlin.math.roundToLong

internal var globalLogLevel: Napier.Level? = null

fun setLogLevel(level: Napier.Level?) {
    globalLogLevel = level
}

expect open class BasicLogger(defaultTag: String? = null) : Antilog


fun Int.formatMag() = toLong().formatMag()
fun Long.formatMag(): String {
    val toString = toString()
    return when {
        this < 1024 -> toString
        this < 1024 * 1024 -> format() + "Ki"
        this < 1024 * 1024 * 1024 -> {
            val m = (toDouble() / 1024.toDouble()).roundToLong()
            m.format() + "Mi"
        }
        else -> {
            val g = (toDouble() / (1024 * 1024).toDouble()).roundToLong()
            g.format() + "Gi"
        }
    }
}

private fun Long.format(): String {
    val mag = floor(this.toDouble() / 1024.0).toInt()
    val toString = (1000 + (this % 1024)).toString().substring(1)
    return "$mag.${toString}"
}