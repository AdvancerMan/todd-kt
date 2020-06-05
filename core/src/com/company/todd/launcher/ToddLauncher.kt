package com.company.todd.launcher

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class ToddLauncher: ApplicationAdapter() {
    lateinit var batch: SpriteBatch private set
    private lateinit var screenManager: ScreenManager

    override fun create() {
        batch = SpriteBatch()
        screenManager = ScreenManager()
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        screenManager.render(Gdx.graphics.deltaTime)
    }

    override fun dispose() {
        batch.dispose()
    }
}
