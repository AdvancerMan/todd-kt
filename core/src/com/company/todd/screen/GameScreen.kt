package com.company.todd.screen

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.QueryCallback
import com.badlogic.gdx.physics.box2d.RayCastCallback
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.creature.Player
import com.company.todd.objects.base.BodyWrapper
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.base.toMeters
import com.company.todd.objects.base.toPix
import com.company.todd.objects.passive.Level
import com.company.todd.box2d.MyContactListener
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.json.ManuallyJsonSerializable
import com.company.todd.json.deserialization.updateFromJson
import com.company.todd.json.serialization.toJsonFull
import com.company.todd.json.serialization.toJsonSave
import com.company.todd.json.serialization.toJsonUpdates
import com.company.todd.json.serialization.toJsonValue
import com.company.todd.thinker.PlayerThinker

open class GameScreen(game: ToddGame, level: Level? = null): MyScreen(game), ManuallyJsonSerializable {
    protected val world = World(Vector2(0f, -30f), true)
    protected val objects = Group()
    protected val playerThinker = PlayerThinker(game)
    protected val justAddedObjects = mutableListOf<InGameObject>()
    protected val justDestroyedObjects = mutableSetOf<InGameObject>()
    val player = Player(game, playerThinker)

    init {
        level?.create(game)?.forEach { addObject(it) }
        addObject(player)
        stage.addActor(objects)
        ScreenActors.addActor(playerThinker)
        stage.addListener(playerThinker.createInputListener())
        world.setContactListener(MyContactListener())
    }

    fun addObject(obj: InGameObject) {
        justAddedObjects.add(obj)
    }

    fun destroyObject(obj: InGameObject) {
        justDestroyedObjects.add(obj)
    }

    protected open fun addObjects() {
        justAddedObjects.forEach { objects.addActor(it.apply { init(this@GameScreen) }) }
        justAddedObjects.clear()
    }

    protected open fun destroyObjects() {
        justDestroyedObjects.forEach {
            objects.removeActor(it)
            it.dispose()
        }
    }

    override fun update(delta: Float) {
        super.update(delta)
        world.step(delta, 10, 10)
        objects.children.forEach { (it as InGameObject).postAct(delta) }

        addObjects()
        destroyObjects()

        centerCameraAt(player.getCenter())
        stage.camera.up.set(Vector2(0f, 1f).rotate(player.rotation), 0f)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        playerThinker.resize(stage.camera.viewportWidth, stage.camera.viewportHeight)
    }

    override fun dispose() {
        playerThinker.dispose()
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

    override fun serializeUpdates(json: JsonValue) {
        if (!json.hasChild("objects")) {
            json.addChild("objects", objects.children.toList().toJsonUpdates())
        }
        json.addChild("worldGravity", world.gravity.toJsonValue())
    }

    override fun deserializeUpdates(json: JsonValue) {
        val updates = json["objects"].associateBy { it["id"].asInt() }
        objects.children.forEach { obj -> updates[obj.hashCode()]?.let { obj.updateFromJson(it) } }
    }

    override fun serializeFull(json: JsonValue) {
        if (!json.hasChild("objects")) {
            json.addChild("objects", objects.children.toList().toJsonFull())
        }
    }

    override fun serializeSave(json: JsonValue) {
        if (!json.hasChild("objects")) {
            json.addChild("objects", objects.children.toList().toJsonSave())
        }
    }
}
