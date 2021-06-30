package com.company.todd.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.getActorAABB
import com.company.todd.util.SCREEN_HEIGHT
import com.company.todd.util.SCREEN_WIDTH

abstract class MyScreen(protected val game: ToddGame) : Screen, Disposable {
    protected val stage = Stage(StretchViewport(
            SCREEN_WIDTH.toFloat(), SCREEN_HEIGHT.toFloat(),
            OrthographicCamera().apply { setToOrtho(false) }
    )).also { Gdx.input.inputProcessor = it }

    protected val screenActors = ScreenActors()

    init {
        stage.addActor(screenActors)
    }

    override fun render(delta: Float) {
        update(delta)
        draw()
    }

    fun getCameraAABB() = screenActors.getActorAABB()

    fun centerCameraAt(x: Float, y: Float) {
        stage.camera.position.x = x
        stage.camera.position.y = y
    }

    fun centerCameraAt(v: Vector2) = centerCameraAt(v.x, v.y)

    open fun update(delta: Float) {
        stage.act(delta)
    }

    open fun draw() {
        screenActors.updateParameters()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun pause() {}

    override fun resume() {
        // TODO Gdx.input.inputProcessor = stage
    }

    override fun hide() {}

    override fun show() {
        // TODO Gdx.input.inputProcessor = stage
    }

    override fun dispose() {
        stage.dispose()
    }

    inner class ScreenActors : Group() {
        fun updateParameters() {
            val viewport = stage.viewport
            val camera = viewport.camera

            setSize(viewport.worldWidth, viewport.worldHeight)
            setPosition(camera.position.x, camera.position.y, Align.center)
            setOrigin(Align.center)
            setScale((camera as OrthographicCamera).zoom)
            rotation = Vector2(camera.up.x, camera.up.y).angle() - 90

            toFront()
        }
    }
}
