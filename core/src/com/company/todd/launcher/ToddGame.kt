package com.company.todd.launcher

import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.company.todd.screen.RainbowScreen
import com.company.todd.util.asset.texture.TextureManager

lateinit var assetsFolder: String private set

class ToddGame: ApplicationAdapter() {
    lateinit var batch: Batch private set
    lateinit var screenManager: ScreenManager private set
    lateinit var textureManager: TextureManager private set

    override fun create() {
        batch = SpriteBatch()
        textureManager = TextureManager()
        screenManager = ScreenManager(RainbowScreen(this, 4f, 5f))
        assetsFolder = if (Gdx.app.type == Application.ApplicationType.Desktop) "android/assets/" else ""
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        screenManager.render(Gdx.graphics.deltaTime)
    }

    override fun dispose() {
        screenManager.pause()
        screenManager.dispose()
        textureManager.dispose()
        batch.dispose()
    }
}
