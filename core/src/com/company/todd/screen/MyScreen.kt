package com.company.todd.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.company.todd.launcher.ToddGame

abstract class MyScreen(protected val game: ToddGame): Screen {
    private val camera = OrthographicCamera()
    private val resizedTo = Vector2(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

    init {
        camera.setToOrtho(false)
    }

    override fun render(delta: Float) {
        update(delta)

        game.batch.projectionMatrix = getCameraProjectionMatrix()
        game.batch.begin()
        draw(game.batch, getCameraRectangle())
        game.batch.end()
    }

    fun getCameraProjectionMatrix() = camera.combined!!

    fun getCameraRectangle() =
            Rectangle(
                    camera.position.x - camera.viewportWidth / 2,
                    camera.position.y - camera.viewportHeight / 2,
                    camera.viewportWidth, camera.viewportHeight
            )

    fun translateCamera(x: Float, y: Float) {
        camera.translate(x, y)
        camera.update()
    }

    fun centerCameraAt(x: Float, y: Float) {
        translateCamera(x - camera.position.x, y - camera.position.y)
    }

    fun centerCameraAt(v: Vector2) = centerCameraAt(v.x, v.y)

    fun setCameraSize(width: Float, height: Float) {
        camera.viewportHeight = height
        camera.viewportWidth = width
        camera.update()
    }

    fun addToCameraSize(deltaWidth: Float, deltaHeight: Float) =
            setCameraSize(camera.viewportWidth + deltaWidth, camera.viewportHeight + deltaHeight)

    fun zoom(scaleX: Float, scaleY: Float) =
            setCameraSize(camera.viewportWidth * scaleX, camera.viewportHeight * scaleY)

    fun zoom(zoom: Float) {
        camera.zoom *= zoom
        camera.update()
    }

    override fun resize(width: Int, height: Int) {
        zoom(width.toFloat() / resizedTo.x, height.toFloat() / resizedTo.y)
        resizedTo.set(width.toFloat(), height.toFloat())
    }

    abstract fun update(delta: Float)
    abstract fun draw(batch: Batch, cameraRect: Rectangle)

    override fun pause() {}
    override fun resume() {}

    override fun hide() {}
    override fun show() {}
}
