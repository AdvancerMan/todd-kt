package com.company.todd.launcher

import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.company.todd.screen.DebugScreen
import com.company.todd.asset.texture.TextureManager
import com.company.todd.screen.ScreenManager
import com.company.todd.util.SPF
import kotlin.math.min

lateinit var assetsFolder: String private set

class ToddGame: ApplicationListener {
    lateinit var screenManager: ScreenManager private set
    lateinit var textureManager: TextureManager private set

    override fun create() {
        assetsFolder = if (Gdx.app.type == Application.ApplicationType.Desktop) "android/assets/" else ""
        textureManager = TextureManager()
        screenManager = ScreenManager(DebugScreen(this))
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        val delta = min(Gdx.graphics.deltaTime, SPF)
        screenManager.render(delta)
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
    }
}
