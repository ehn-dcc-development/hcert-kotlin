package ehn.techiop.hcert.kotlin.log

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock

//was once based on default JS debug Antilog

actual open class BasicLogger actual constructor(protected val defaultTag: String?) : Antilog() {

    override fun performLog(priority: LogLevel, tag: String?, throwable: Throwable?, message: String?) {
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

    private fun setupTag(priority: LogLevel, tag: String?): String {
        val logTag = tag ?: defaultTag ?: ""
        return Clock.System.now().toString() + if (logTag.isEmpty()) " $priority:" else " $priority $logTag:"
    }

    private fun log(priority: LogLevel, msg: String) {
        when (priority) {
            LogLevel.VERBOSE, LogLevel.DEBUG -> console.log(msg)
            LogLevel.INFO -> console.info(msg)
            LogLevel.WARNING -> console.warn(msg)
            LogLevel.ERROR, LogLevel.ASSERT -> console.error(msg)
        }
    }
}

@JsExport
@Suppress("NON_EXPORTABLE_TYPE")
class JsLogger(private val loggingFunction: (level: String, tag: String?, stackTrace: String?, message: String?) -> Unit) :
    Antilog() {
    override fun performLog(priority: LogLevel, tag: String?, throwable: Throwable?, message: String?) {
        if (globalLogLevel != null)
            loggingFunction(priority.name, tag, throwable?.stackTraceToString(), message)
    }
}