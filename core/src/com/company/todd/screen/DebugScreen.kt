package com.company.todd.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.toPix
import com.company.todd.objects.passive.level.loadLevels
import com.company.todd.util.input.MovingInputType

class DebugScreen(game: ToddGame): GameScreen(game) {
    private val renderer = Box2DDebugRenderer()
    private var pressedPlay = true
    private var debugDraw = true

    init {
        loadLevels()
                .find { it.name == "testLevel" }
                .let { it ?: error("testLevel is not found") }
                .create(game)
                .map { addObject(it) }
    }

    override fun render(delta: Float) {
        super.render(delta)
        if (debugDraw) {
            renderer.render(world, stage.camera.combined.cpy().toPix())
        }
        if (player.getCenter().y < -200f) {
            player.setCenter(0f, 0f)
        }
    }

    override fun update(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            playerInputActor.setInputActor(MovingInputType.SLIDER)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
            playerInputActor.setInputActor(MovingInputType.TOUCHPAD)
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

    override fun dispose() {
        super.dispose()
        renderer.dispose()
    }
}
