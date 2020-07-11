package com.company.todd.screen

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject

open class GameScreen(game: ToddGame): MyScreen(game) {
    val world = World(Vector2(0f, -10f), true)
    protected val objects = Group()
    protected val justAddedObjects = mutableListOf<InGameObject>()

    init {
        stage.addActor(objects)
    }

    fun addObject(obj: InGameObject) {
        justAddedObjects.add(obj)
    }

    override fun update(delta: Float) {
        super.update(delta)
        world.step(delta, 10, 10)
        objects.children.forEach { (it as InGameObject).postAct(delta) }

        justAddedObjects.forEach { objects.addActor(it.apply { init() }) }
        justAddedObjects.clear()
    }

    override fun dispose() {
        justAddedObjects.forEach { it.dispose() }
        justAddedObjects.clear()
        objects.children.forEach { (it as InGameObject).dispose() }
        world.dispose()
        super.dispose()
    }
}
