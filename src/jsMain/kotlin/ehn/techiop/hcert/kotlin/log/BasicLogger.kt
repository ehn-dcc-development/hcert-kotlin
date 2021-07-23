package ehn.techiop.hcert.kotlin.log

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock

//was once based on default JS debug Antilog

actual open class BasicLogger actual constructor(protected val defaultTag: String?) : Antilog() {

    override fun performLog(priority: Napier.Level, tag: String?, throwable: Throwable?, message: String?) {
        if (tag != null && defaultTag != null && tag != defaultTag)
            return

        val fullMessage = if (message != null) {
            if (throwable != null) "$message\n${throwable.stackTraceToString()}" else message
        } else throwable?.message ?: return

        globalLogLevel?.let { setLevel ->
            if (setLevel.ordinal <= priority.ordinal)
                log(priority, "${setupTag(priority, tag)} $fullMessage\n")
        }
    }

    private fun setupTag(priority: Napier.Level, tag: String?): String {
        val logTag = tag ?: defaultTag ?: ""
        return Clock.System.now().toString() + if (logTag.isEmpty()) " $priority:" else " $priority $logTag:"
    }

    private fun log(priority: Napier.Level, msg: String) {
        when (priority) {
            Napier.Level.VERBOSE, Napier.Level.DEBUG -> console.log(msg)
            Napier.Level.INFO -> console.info(msg)
            Napier.Level.WARNING -> console.warn(msg)
            Napier.Level.ERROR, Napier.Level.ASSERT -> console.error(msg)
        }
    }
}

@JsExport
@Suppress("NON_EXPORTABLE_TYPE")
class JsLogger(private val loggingFunction: (level: String, tag: String?, stackTrace: String?, message: String?) -> Unit) :
    Antilog() {
    override fun performLog(priority: Napier.Level, tag: String?, throwable: Throwable?, message: String?) {
        if (globalLogLevel != null)
            loggingFunction(priority.name, tag, throwable?.stackTraceToString(), message)
    }
}