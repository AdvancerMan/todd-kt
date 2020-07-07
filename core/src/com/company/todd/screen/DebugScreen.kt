package com.company.todd.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.toPix

class DebugScreen(game: ToddGame): GameScreen(game) {
    private val renderer = Box2DDebugRenderer()
    private var pressedPlay = true
    private var debugDraw = true

    override fun render(delta: Float) {
        update(delta)
        if (debugDraw) {
            renderer.render(world, getCameraProjectionMatrix().cpy().toPix())
        } else {
            draw(game.batch, getCameraRectangle())
        }
    }

    override fun update(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            pressedPlay = !pressedPlay
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || Gdx.input.isKeyPressed(Input.Keys.W) || pressedPlay) {
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
