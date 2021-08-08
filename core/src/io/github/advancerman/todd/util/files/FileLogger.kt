package io.github.advancerman.todd.util.files

import com.badlogic.gdx.ApplicationLogger
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

class FileLogger(fileName: String, private val innerLogger: ApplicationLogger? = null)
    : MyApplicationLogger, Disposable {
    private val printer = PrintWriter(Gdx.files.local(fileName).writer(false, Charsets.UTF_8.toString()))

    @Suppress("SimpleDateFormat")
    private fun formatMessage(level: String, tag: String, message: String): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return "[$level][${dateFormat.format(Calendar.getInstance().time)}][$tag] $message"
    }

    override fun log(tag: String, message: String) {
        printer.println(formatMessage("INFO", tag, message))
        printer.flush()
        innerLogger?.log(tag, message)
    }

    override fun log(tag: String, message: String, exception: Throwable) {
        printer.println(formatMessage("INFO", tag, message))
        exception.printStackTrace(printer)
        printer.flush()
        innerLogger?.log(tag, message, exception)
    }

    override fun error(tag: String, message: String) {
        printer.println(formatMessage("ERROR", tag, message))
        printer.flush()
        innerLogger?.error(tag, message)
    }

    override fun error(tag: String, message: String, exception: Throwable) {
        printer.println(formatMessage("ERROR", tag, message))
        exception.printStackTrace(printer)
        printer.flush()
        innerLogger?.error(tag, message, exception)
    }

    override fun debug(tag: String, message: String) {
        printer.println(formatMessage("DEBUG", tag, message))
        printer.flush()
        innerLogger?.debug(tag, message)
    }

    override fun debug(tag: String, message: String, exception: Throwable) {
        printer.println(formatMessage("DEBUG", tag, message))
        exception.printStackTrace(printer)
        printer.flush()
        innerLogger?.debug(tag, message, exception)
    }

    override fun dispose() {
        printer.close()
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        error("UncaughtException", "Unexpected error occurred in thread ${thread.name}", throwable)
    }
}
