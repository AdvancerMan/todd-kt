package io.github.advancerman.todd.launcher

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable
import io.github.advancerman.todd.asset.font.FontManager
import io.github.advancerman.todd.screen.game.DebugScreen
import io.github.advancerman.todd.asset.texture.TextureManager
import io.github.advancerman.todd.screen.ScreenManager
import io.github.advancerman.todd.screen.menu.MainMenuScreen
import io.github.advancerman.todd.util.SPF
import io.github.advancerman.todd.util.files.FileLogger
import io.github.advancerman.todd.util.files.MyApplicationLogger
import io.github.advancerman.todd.util.withExceptionHandler
import kotlin.math.min

// TODO remove, is needed to pass through deserialization of behaviours list
lateinit var game: ToddGame

class ToddGame: ApplicationListener {
    lateinit var logger: MyApplicationLogger private set
    lateinit var screenManager: ScreenManager private set
    lateinit var textureManager: TextureManager private set
    lateinit var fontManager: FontManager private set

    override fun create() {
        logger = FileLogger("todd.log", Gdx.app.applicationLogger)
        Gdx.app.applicationLogger = logger
        Thread.currentThread().withExceptionHandler(logger)

        textureManager = TextureManager()
        fontManager = FontManager()
        screenManager = ScreenManager(MainMenuScreen(this))
        game = this
    }

    override fun render() {
        val delta = min(Gdx.graphics.deltaTime, SPF)
        screenManager.render(delta)
        screenManager.update()
        textureManager.update(delta)
        fontManager.update(delta)
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
