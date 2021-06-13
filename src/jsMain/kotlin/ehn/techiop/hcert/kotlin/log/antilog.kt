package ehn.techiop.hcert.kotlin.log

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock

//based on default JS debug Antilog
internal actual fun antilog(defaultTag: String?) = object : Antilog() {

    override fun performLog(priority: Napier.Level, tag: String?, throwable: Throwable?, message: String?) {
        if (tag != null && defaultTag != null && tag != defaultTag)
            return

        val logTag = tag ?: defaultTag ?: ""

        val fullMessage = if (message != null) {
            if (throwable != null) {
                "$message\n${throwable.stackTraceToString()}"
            } else {
                message
            }
        } else throwable?.message ?: return

        globalLogLevel?.let { setLevel ->
            if (setLevel.ordinal <= priority.ordinal)
                when (priority) {
                    Napier.Level.VERBOSE -> console.log("${Clock.System.now()} VERBOSE $logTag : $fullMessage\n")
                    Napier.Level.DEBUG -> console.log("${Clock.System.now()} DEBUG $logTag : $fullMessage\n")
                    Napier.Level.INFO -> console.info("${Clock.System.now()} INFO $logTag : $fullMessage\n")
                    Napier.Level.WARNING -> console.warn("${Clock.System.now()} WARNING $logTag : $fullMessage\n")
                    Napier.Level.ERROR -> console.error("${Clock.System.now()} ERROR $logTag : $fullMessage\n")
                    Napier.Level.ASSERT -> console.error("${Clock.System.now()} ASSERT $logTag : $fullMessage\n")
                }
        }
    }
}
