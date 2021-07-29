package com.company.todd.launcher

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable
import com.company.todd.screen.game.DebugScreen
import com.company.todd.asset.texture.TextureManager
import com.company.todd.screen.ScreenManager
import com.company.todd.screen.menu.MainMenuScreen
import com.company.todd.util.SPF
import com.company.todd.util.files.FileLogger
import com.company.todd.util.files.MyApplicationLogger
import com.company.todd.util.withExceptionHandler
import kotlin.math.min

class ToddGame: ApplicationListener {
    lateinit var logger: MyApplicationLogger private set
    lateinit var screenManager: ScreenManager private set
    lateinit var textureManager: TextureManager private set

    override fun create() {
        logger = FileLogger("todd.log", Gdx.app.applicationLogger)
        Gdx.app.applicationLogger = logger
        Thread.currentThread().withExceptionHandler(logger)

        textureManager = TextureManager()
        screenManager = ScreenManager(MainMenuScreen(this))
    }

    override fun render() {
        val delta = min(Gdx.graphics.deltaTime, SPF)
        screenManager.render(delta)
        screenManager.update()
        textureManager.update(delta)
    }

    override fun resize(width: Int, height: Int) {
        screenManager.resize(width, height)
    }

    override fun pause() {
        screenManager.pause()
    }

    override fun resume() {
        screenManager.resume()
    }

    override fun dispose() {
        screenManager.pause()
        screenManager.dispose()
        textureManager.dispose()
        if (logger is Disposable) {
            (logger as Disposable).dispose()
        }
    }
}
