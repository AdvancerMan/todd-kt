package com.company.todd.launcher

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.company.todd.screen.RainbowScreen

class ToddGame: ApplicationAdapter() {
    lateinit var batch: Batch private set
    lateinit var screenManager: ScreenManager private set

    override fun create() {
        batch = SpriteBatch()
        screenManager = ScreenManager(RainbowScreen(this, 4f, 5f))
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        screenManager.render(Gdx.graphics.deltaTime)
    }

    override fun dispose() {
        screenManager.pause()
        screenManager.dispose()
        batch.dispose()
    }
}
