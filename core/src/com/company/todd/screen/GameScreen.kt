package com.company.todd.screen

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.creature.friendly.Player
import com.company.todd.objects.base.InGameObject
import com.company.todd.util.box2d.MyContactListener
import com.company.todd.util.input.PlayerInputActor

open class GameScreen(game: ToddGame): MyScreen(game) {
    val world = World(Vector2(0f, -30f), true)
    protected val objects = Group()
    private val playerInputActor = PlayerInputActor().also { stage.addListener(it.createListener()) }
    protected val justAddedObjects = mutableListOf<InGameObject>()
    protected val player = Player(game, playerInputActor).also { addObject(it) }

    init {
        stage.addActor(objects)
        world.setContactListener(MyContactListener())
    }

    fun addObject(obj: InGameObject) {
        justAddedObjects.add(obj)
    }

    override fun update(delta: Float) {
        super.update(delta)
        world.step(delta, 10, 10)
        objects.children.forEach { (it as InGameObject).postAct(delta) }

        justAddedObjects.forEach { objects.addActor(it.apply { init(this@GameScreen) }) }
        justAddedObjects.clear()

        centerCameraAt(player.getCenter())
    }

    override fun dispose() {
        justAddedObjects.forEach { it.dispose() }
        justAddedObjects.clear()
        objects.children.forEach { (it as InGameObject).dispose() }
        world.dispose()
        super.dispose()
    }
}
