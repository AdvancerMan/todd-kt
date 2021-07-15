package com.company.todd.util.files

import com.badlogic.gdx.ApplicationLogger
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable
import java.io.PrintWriter

class FileLogger(fileName: String, private val innerLogger: ApplicationLogger? = null) : ApplicationLogger, Disposable {
    private val printer = PrintWriter(Gdx.files.local(fileName).writer(false, Charsets.UTF_8.toString()))

    override fun log(tag: String, message: String) {
        printer.println("[$tag] $message")
        printer.flush()
        innerLogger?.log(tag, message)
    }

    override fun log(tag: String, message: String, exception: Throwable) {
        printer.println("[$tag] $message")
        exception.printStackTrace(printer)
        printer.flush()
        innerLogger?.log(tag, message, exception)
    }

    override fun error(tag: String, message: String) {
        printer.println("[$tag] $message")
        printer.flush()
        innerLogger?.error(tag, message)
    }

    override fun error(tag: String, message: String, exception: Throwable) {
        printer.println("[$tag] $message")
        exception.printStackTrace(printer)
        printer.flush()
        innerLogger?.error(tag, message, exception)
    }

    override fun debug(tag: String, message: String) {
        printer.println("[$tag] $message")
        printer.flush()
        innerLogger?.debug(tag, message)
    }

    override fun debug(tag: String, message: String, exception: Throwable) {
        printer.println("[$tag] $message")
        exception.printStackTrace(printer)
        printer.flush()
        innerLogger?.debug(tag, message, exception)
    }

    override fun dispose() {
        printer.close()
    }
}
