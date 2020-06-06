package com.company.todd.screen

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject

open class GameScreen(game: ToddGame): MyScreen(game) {
    val world = World(Vector2(0f, -10f), true)
    private val objects = mutableListOf<InGameObject>()
    private val justAddedObjects = mutableListOf<InGameObject>()

    override fun update(delta: Float) {
        objects.forEach { it.preUpdate(delta) }
        world.step(delta, 10, 10)
        objects.forEach { it.postUpdate(delta) }

        justAddedObjects.forEach { objects.add(it) }
        justAddedObjects.clear()
    }

    override fun draw(batch: SpriteBatch, cameraRect: Rectangle) =
            objects.forEach { it.draw(batch, cameraRect) }

    override fun dispose() {
        justAddedObjects.forEach { it.dispose() }
        justAddedObjects.clear()
        objects.forEach { it.dispose() }
        objects.clear()
        world.dispose()
    }
}
