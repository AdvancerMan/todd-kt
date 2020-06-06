package com.company.todd.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.toPix

class DebugScreen(game: ToddGame): GameScreen(game) {
    private val renderer = Box2DDebugRenderer()
    private var pressedPlay = true

    override fun render(delta: Float) {
        update(delta)
        renderer.render(world, getCameraProjectionMatrix().cpy().toPix())
    }

    override fun update(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            pressedPlay = !pressedPlay
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || Gdx.input.isKeyPressed(Input.Keys.W) || pressedPlay) {
            super.update(delta)
        }
    }

    override fun dispose() {
        super.dispose()
        renderer.dispose()
    }
}
