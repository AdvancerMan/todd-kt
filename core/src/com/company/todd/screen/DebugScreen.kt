package com.company.todd.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.toPix
import com.company.todd.objects.passive.platform.SolidRectanglePlatform

class DebugScreen(game: ToddGame): GameScreen(game) {
    private val renderer = Box2DDebugRenderer()
    private var pressedPlay = true
    private var debugDraw = true

    init {
        listOf(
                listOf(-50, -50, 100, 25),
                listOf(-50, 100, 100, 25)
        )
                .map { it.map { x -> x.toFloat() } }
                .map { Rectangle(it[0], it[1], it[2], it[3]) }
                .map { SolidRectanglePlatform(game, game.textureManager.loadSprite("solid"), it) }
                .forEach { addObject(it) }
    }

    override fun render(delta: Float) {
        update(delta)
        if (debugDraw) {
            stage.camera.update()
            renderer.render(world, stage.camera.combined.cpy().toPix())
        } else {
            draw()
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
