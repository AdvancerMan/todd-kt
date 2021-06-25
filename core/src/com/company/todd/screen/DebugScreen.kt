package com.company.todd.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.company.todd.launcher.ToddGame
import com.company.todd.json.loadLevels
import com.company.todd.objects.base.pixInMeter
import com.company.todd.input.MovingInputType

class DebugScreen(game: ToddGame): GameScreen(game, loadLevels().find { it.name == "testLevel" }) {
    private val debugRenderer = Box2DDebugRenderer()
    private var pressedPlay = true
    private var debugDraw = true
    private val font = BitmapFont()

    override fun render(delta: Float) {
        super.render(delta)
        if (debugDraw) {
            debugDraw()
        }

        if (player.initialized && player.getCenter().y < -200f) {
            player.setPosition(0f, 0f, true)
        }
    }

    override fun update(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            playerInputActor.setInputActorType(MovingInputType.SLIDER)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
            playerInputActor.setInputActorType(MovingInputType.TOUCHPAD)
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
}
