package com.company.todd.launcher

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.utils.Queue
import java.lang.UnsupportedOperationException

class ScreenManager(): Screen {
    private val stack = Queue<Screen>(4)
    private var width = Gdx.graphics.width
    private var height = Gdx.graphics.height
    private val doOnUpdate = Queue<() -> Unit>(2)

    constructor(firstScreen: Screen): this() {
        ScreenManager()
        push(firstScreen)
    }

    fun update() {
        doOnUpdate.forEach { it() }
        doOnUpdate.clear()
    }

    private fun naivePop() {
        pause()
        stack.removeLast().dispose()
    }

    private fun realPop() {
        naivePop()
        resume()
    }

    fun pop() {
        doOnUpdate.addLast(this::realPop)
    }

    private fun naivePush(screen: Screen) {
        stack.addLast(screen)
        resume()
        resize(width, height)
    }

    fun push(screen: Screen) {
        pause()
        naivePush(screen)
    }

    fun replaceLast(screen: Screen) {
        doOnUpdate.addLast {
            naivePop()
            naivePush(screen)
        }
    }

    fun replaceAll(screen: Screen) {
        doOnUpdate.addLast {
            pause()
            dispose()
            naivePush(screen)
        }
    }

    override fun hide() = throw UnsupportedOperationException()
    override fun show() = throw UnsupportedOperationException()

    private fun doWithLast(f: Screen.() -> Unit) {
        if (!stack.isEmpty) {
            stack.last().f()
        }
    }

    override fun render(delta: Float) = doWithLast { render(delta) }
    override fun resume() = doWithLast(Screen::resume)
    override fun pause() = doWithLast(Screen::pause)

    override fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
        doWithLast { resize(width, height) }
    }

    override fun dispose() {
        stack.forEach(Screen::dispose)
        stack.clear()
    }
}
