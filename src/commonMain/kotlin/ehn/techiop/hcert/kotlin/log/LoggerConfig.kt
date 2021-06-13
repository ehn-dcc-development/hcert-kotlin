package ehn.techiop.hcert.kotlin.log

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.Napier


internal var globalLogLevel: Napier.Level? = null

fun setLogLevel(level: Napier.Level?) {
    globalLogLevel = level
}

internal expect fun antilog(defaultTag: String? = null): Antilog