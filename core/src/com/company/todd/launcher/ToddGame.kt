package com.company.todd.launcher

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.company.todd.screen.game.DebugScreen
import com.company.todd.asset.texture.TextureManager
import com.company.todd.screen.ScreenManager
import com.company.todd.screen.menu.MainMenuScreen
import com.company.todd.util.SPF
import com.company.todd.util.files.FileLogger
import kotlin.math.min

class ToddGame: ApplicationListener {
    lateinit var logger: FileLogger private set
    lateinit var screenManager: ScreenManager private set
    lateinit var textureManager: TextureManager private set

    override fun create() {
        try {
            logger = FileLogger("todd.log", Gdx.app.applicationLogger)
            Gdx.app.applicationLogger = logger
            textureManager = TextureManager()
            screenManager = ScreenManager(MainMenuScreen(this))
        } catch (e: Exception) {
            Gdx.app.error("ToddGame", "An error occurred during game creation", e)
            throw e
        }
    }

    override fun render() {
        try {
            val delta = min(Gdx.graphics.deltaTime, SPF)
            screenManager.render(delta)
            screenManager.update()
            textureManager.update(delta)
        } catch (e: Exception) {
            Gdx.app.error("ToddGame", "An error occurred during game render", e)
            throw e
        }
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
        logger.dispose()
    }
}
