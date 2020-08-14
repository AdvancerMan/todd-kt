package com.company.todd.screen

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.QueryCallback
import com.badlogic.gdx.physics.box2d.RayCastCallback
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.creature.friendly.Player
import com.company.todd.objects.base.BodyWrapper
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.base.toMeters
import com.company.todd.objects.base.toPix
import com.company.todd.objects.passive.Level
import com.company.todd.box2d.MyContactListener
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.input.PlayerInputActor

open class GameScreen(game: ToddGame, level: Level? = null): MyScreen(game) {
    protected val world = World(Vector2(0f, -30f), true)
    protected val objects = Group()
    protected val playerInputActor = PlayerInputActor(game)
    protected val justAddedObjects = mutableListOf<InGameObject>()
    val player = Player(game, playerInputActor)

    init {
        level?.create(game)?.forEach { addObject(it) }
        addObject(player)
        stage.addActor(objects)
        ScreenActors.addActor(playerInputActor)
        stage.addListener(playerInputActor.createInputListener())
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
        stage.camera.up.set(Vector2(0f, 1f).rotate(player.rotation), 0f)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        playerInputActor.resize(stage.camera.viewportWidth, stage.camera.viewportHeight)
    }

    override fun dispose() {
        playerInputActor.dispose()
        justAddedObjects.forEach { it.dispose() }
        justAddedObjects.clear()
        objects.children.forEach { (it as InGameObject).dispose() }
        world.dispose()
        super.dispose()
    }

    // world methods
    fun destroyBody(body: BodyWrapper) =
            body.destroy(world)

    fun createBody(pattern: BodyPattern) =
            pattern.createBody(world)

    fun getGravity() =
            world.gravity.cpy().toPix()

    fun queryAABB(lowerX: Float, lowerY: Float, upperX: Float, upperY: Float, callback: (Fixture) -> Boolean) =
            world.QueryAABB(callback, lowerX.toMeters(), lowerY.toMeters(), upperX.toMeters(), upperY.toMeters())

    fun queryAABB(lowerX: Float, lowerY: Float, upperX: Float, upperY: Float, callback: QueryCallback) =
            world.QueryAABB(callback, lowerX.toMeters(), lowerY.toMeters(), upperX.toMeters(), upperY.toMeters())

    fun rayCast(point1X: Float, point1Y: Float, point2X: Float, point2Y: Float, callback: RayCastCallback) =
            world.rayCast(callback, point1X.toMeters(), point1Y.toMeters(), point2X.toMeters(), point2Y.toMeters())

    fun rayCast(point1X: Float, point1Y: Float, point2X: Float, point2Y: Float, callback: (Fixture, Vector2, Vector2, Float) -> Float) =
            world.rayCast(callback, point1X.toMeters(), point1Y.toMeters(), point2X.toMeters(), point2Y.toMeters())
}
