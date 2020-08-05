package com.company.todd.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.company.todd.launcher.ToddGame
import com.company.todd.util.SCREEN_HEIGHT
import com.company.todd.util.SCREEN_WIDTH

abstract class MyScreen(protected val game: ToddGame): Screen, Disposable {
    protected val stage = Stage(StretchViewport(
            SCREEN_WIDTH.toFloat(), SCREEN_HEIGHT.toFloat(),
            OrthographicCamera().apply { setToOrtho(false) }
    )).also { Gdx.input.inputProcessor = it }

    override fun render(delta: Float) {
        update(delta)
        draw()
    }

    fun getCameraAABB() =
            stage.screenToStageCoordinates(Vector2(0f, 0f)).let {
                val w = stage.viewport.screenWidth.toFloat()
                val h = stage.viewport.screenHeight.toFloat()
                Rectangle(it.x, it.y - h, w, h)
            }

    fun centerCameraAt(x: Float, y: Float) {
        stage.camera.position.x = x
        stage.camera.position.y = y
    }

    fun centerCameraAt(v: Vector2) = centerCameraAt(v.x, v.y)

    open fun update(delta: Float) {
        stage.act(delta)
    }

    open fun draw() {
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun pause() {}
    override fun resume() {}

    override fun hide() {}
    override fun show() {}

    override fun dispose() {
        stage.dispose()
    }
}
