package io.github.advancerman.todd.util.files

import com.badlogic.gdx.ApplicationLogger

interface MyApplicationLogger : ApplicationLogger, Thread.UncaughtExceptionHandler {
    fun doIfUnique(text: String, log: (String) -> Unit)
}
