package com.company.todd.screen.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.company.todd.launcher.ToddGame
import com.company.todd.json.deserialization.loadLevels
import com.company.todd.objects.base.pixInMeter
import com.company.todd.objects.passive.Level
import com.company.todd.thinker.MovingInputType

class DebugScreen(game: ToddGame, level: Level? = loadLevels().find { it.name == "testLevel" }): GameScreen(game, level) {
    private val debugRenderer = Box2DDebugRenderer()
    private var pressedPlay = true
    private var debugDraw = true
    private val font = BitmapFont()

    override fun render(delta: Float) {
        super.render(delta)
        if (debugDraw) {
            debugDraw()
        }

        if (player.initialized && player.body.getCenter().y < -200f) {
            player.body.setPosition(0f, 0f)
        }
    }

    override fun update(delta: Float) {
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
