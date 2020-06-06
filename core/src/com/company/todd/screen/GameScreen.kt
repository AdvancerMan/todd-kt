package com.company.todd.screen

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.company.todd.launcher.ToddGame

open class GameScreen(game: ToddGame): MyScreen(game) {
    val world: World = World(Vector2(0f, -10f), true)

    override fun update(delta: Float) {
        TODO("Not yet implemented")
    }

    override fun draw(batch: SpriteBatch, cameraRect: Rectangle) {
        TODO("Not yet implemented")
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }
}
