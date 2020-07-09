package com.company.todd.screen

import com.badlogic.gdx.Screen
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.company.todd.launcher.ToddGame

abstract class MyScreen(protected val game: ToddGame): Screen, Disposable {
    protected val stage = Stage(ScreenViewport())

    override fun render(delta: Float) {
        update(delta)
        draw()
    }

    fun centerCameraAt(x: Float, y: Float) {
        stage.camera.translate(x - stage.camera.position.x, y - stage.camera.position.y, 0f)
    }

    fun centerCameraAt(v: Vector2) = centerCameraAt(v.x, v.y)

    open fun update(delta: Float) {
        stage.act(delta)
    }

    open fun draw() {
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {}

    override fun pause() {}
    override fun resume() {}

    override fun hide() {}
    override fun show() {}

    override fun dispose() {
        stage.dispose()
    }
}
