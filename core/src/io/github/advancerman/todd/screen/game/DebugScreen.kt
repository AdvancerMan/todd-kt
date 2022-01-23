package io.github.advancerman.todd.screen.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.json.deserialization.loadLevels
import io.github.advancerman.todd.objects.base.pixInMeter
import io.github.advancerman.todd.objects.passive.Level
import io.github.advancerman.todd.screen.MyScreen
import io.github.advancerman.todd.thinker.MovingInputType

class DebugScreen(game: ToddGame, level: Level? = loadLevels().find { it.name == "testLevel" }): GameScreen(game, level) {
    private val debugRenderer = Box2DDebugRenderer()
    private var pressedPlay = true
    private var debugDraw = true
    private val font = BitmapFont()
    private var freeCameraEnabled = false
    private val cameraCenter = Vector2()

    override fun render(delta: Float) {
        super.render(delta)
        if (debugDraw) {
            debugDraw()
        }

        if (player.initialized && player.body.getCenter().y < -200f) {
            player.body.setPosition(0f, 0f)
        }
    }

    private fun updateFreeCamera() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
            freeCameraEnabled = !freeCameraEnabled
        }
        if (Gdx.input.isKeyPressed(Input.Keys.I)) {
            cameraCenter.add(0f, 1f * FREE_CAMERA_SPEED)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.K)) {
            cameraCenter.add(0f, -1f * FREE_CAMERA_SPEED)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.J)) {
            cameraCenter.add(-1f * FREE_CAMERA_SPEED, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.L)) {
            cameraCenter.add(1f * FREE_CAMERA_SPEED, 0f)
        }
    }

    override fun update(delta: Float) {
        updateFreeCamera()
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            playerThinker.setMovingActor(MovingInputType.SLIDER)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
            playerThinker.setMovingActor(MovingInputType.TOUCHPAD)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            playerThinker.setMovingActor(MovingInputType.MOVING_BUTTONS)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            pressedPlay = !pressedPlay
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || Gdx.input.isKeyPressed(Input.Keys.F) || pressedPlay) {
            super.update(delta)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            debugDraw = !debugDraw
        }
    }

    override fun postUpdate(delta: Float) {
        super.postUpdate(delta)
        if (!freeCameraEnabled) {
            cameraCenter.set(player.body.getCenter())
        } else {
            stage.camera.up.set(Vector2(0f, 1f), 0f)
            centerCameraAt(cameraCenter)
            screenActors.updateParameters()
        }
    }

    fun debugDraw() {
        (stage.camera as OrthographicCamera).zoom /= pixInMeter
        stage.camera.position.scl(1 / pixInMeter, 1 / pixInMeter, 1f)
        stage.camera.update()
        debugRenderer.render(world, stage.camera.combined)
        stage.camera.position.scl(pixInMeter, pixInMeter, 1f)
        (stage.camera as OrthographicCamera).zoom *= pixInMeter
        stage.camera.update()

        stage.batch.begin()
        stage.batch.projectionMatrix = stage.camera.projection
        font.draw(stage.batch, "fps: " + Gdx.graphics.framesPerSecond,
                -stage.viewport.worldWidth / 2 + 5f, stage.viewport.worldHeight / 2 - 15f)
        stage.batch.end()
    }

    override fun dispose() {
        super.dispose()
        debugRenderer.dispose()
        font.dispose()
    }

    companion object {
        private const val FREE_CAMERA_SPEED = 20f
    }
}
