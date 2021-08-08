package io.github.advancerman.todd.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.StretchViewport
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.objects.base.getActorAABB
import io.github.advancerman.todd.util.SCREEN_HEIGHT
import io.github.advancerman.todd.util.SCREEN_WIDTH

abstract class MyScreen(protected val game: ToddGame) : Screen, Disposable, PostUpdatable {
    protected val stage = Stage(StretchViewport(
            SCREEN_WIDTH.toFloat(), SCREEN_HEIGHT.toFloat(),
            OrthographicCamera().apply { setToOrtho(false) }
    )).also { Gdx.input.inputProcessor = it }

    protected val screenActors = ScreenActors()

    init {
        stage.addActor(screenActors)
    }

    override fun render(delta: Float) {
        do {
            update(delta)
            postUpdate(delta)
        } while (shouldSkipFrame())
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

    override fun postUpdate(delta: Float) {
        screenActors.updateParameters()
    }

    protected open fun shouldSkipFrame() = false

    open fun draw() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

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
            rotation = Vector2(camera.up.x, camera.up.y).angleDeg() - 90

            toFront()
        }
    }
}
