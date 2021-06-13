package ehn.techiop.hcert.kotlin.log

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.Napier
import java.io.PrintWriter
import java.io.StringWriter
import java.util.logging.*
import java.util.regex.Pattern

//based on default JVM debug Antilog
internal actual fun antilog(defaultTag: String?) = object : Antilog() {
    private val handler: List<Handler> = listOf()

    private val CALL_STACK_INDEX = 8

    val consoleHandler: ConsoleHandler = ConsoleHandler().apply {
        level = Level.ALL
        formatter = SimpleFormatter()

    }

    private val logger: Logger = Logger.getLogger(this::class.java.name).apply {
        level = Level.ALL

        //prevent double logging
        useParentHandlers = false

        if (handler.isEmpty()) {
            addHandler(consoleHandler)
            return@apply
        }
        handler.forEach {
            addHandler(it)
        }
    }

    private val anonymousClass = Pattern.compile("(\\$\\d+)+$")

    override fun performLog(priority: Napier.Level, tag: String?, throwable: Throwable?, message: String?) {
        if (tag != null && defaultTag != null && tag != defaultTag)
            return
        val debugTag = tag ?: performTag(defaultTag ?: "")

        val fullMessage = if (message != null) {
            if (throwable != null) {
                "$message\n${throwable.stackTraceString}"
            } else {
                message
            }
        } else throwable?.stackTraceString ?: return

        globalLogLevel?.let { setLevel ->
            if (setLevel.ordinal <= priority.ordinal)
                when (priority) {
                    Napier.Level.VERBOSE -> logger.finest(buildLog(debugTag, fullMessage))
                    Napier.Level.DEBUG -> logger.fine(buildLog(debugTag, fullMessage))
                    Napier.Level.INFO -> logger.info(buildLog(debugTag, fullMessage))
                    Napier.Level.WARNING -> logger.warning(buildLog(debugTag, fullMessage))
                    Napier.Level.ERROR -> logger.severe(buildLog(debugTag, fullMessage))
                    Napier.Level.ASSERT -> logger.severe(buildLog(debugTag, fullMessage))
                }
        }
    }


    private fun buildLog(tag: String?, message: String?): String {
        val src = try {
            Thread.currentThread().stackTrace.drop(4)
                .firstOrNull { !it.className.startsWith("io.github.aakira.napier") } ?: "Antilog"
        } catch (t: Throwable) {
            "Antilog"
        }
        return "$src\n\t${tag ?: performTag(defaultTag ?: "")} - $message"
    }

    private fun performTag(defaultTag: String): String {
        val thread = Thread.currentThread().stackTrace

        return if (thread != null && thread.size >= CALL_STACK_INDEX) {
            thread[CALL_STACK_INDEX].run {
                "${createStackElementTag(className)}\$$methodName"
            }
        } else {
            defaultTag
        }
    }

    private fun createStackElementTag(className: String): String {
        var tag = className
        val m = anonymousClass.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        return tag.substring(tag.lastIndexOf('.') + 1)
    }

    private val Throwable.stackTraceString
        get(): String {
            // DO NOT replace this with Log.getStackTraceString() - it hides UnknownHostException, which is
            // not what we want.
            val sw = StringWriter(256)
            val pw = PrintWriter(sw, false)
            printStackTrace(pw)
            pw.flush()
            return sw.toString()
        }
}
