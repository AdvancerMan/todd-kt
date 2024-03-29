package io.github.advancerman.todd.screen.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.QueryCallback
import com.badlogic.gdx.physics.box2d.RayCastCallback
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.objects.base.BodyWrapper
import io.github.advancerman.todd.objects.base.InGameObject
import io.github.advancerman.todd.objects.base.toMeters
import io.github.advancerman.todd.objects.base.toPix
import io.github.advancerman.todd.objects.passive.Level
import io.github.advancerman.todd.box2d.MyContactListener
import io.github.advancerman.todd.box2d.bodyPattern.base.BodyPattern
import io.github.advancerman.todd.json.ManuallyJsonSerializable
import io.github.advancerman.todd.json.deserialization.*
import io.github.advancerman.todd.json.serialization.toJsonFull
import io.github.advancerman.todd.json.serialization.toJsonSave
import io.github.advancerman.todd.json.serialization.toJsonUpdates
import io.github.advancerman.todd.json.serialization.toJsonValue
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.screen.MyScreen
import io.github.advancerman.todd.screen.PostUpdatable
import io.github.advancerman.todd.thinker.PlayerThinker
import io.github.advancerman.todd.thinker.operated.ThinkerAction

open class GameScreen(game: ToddGame, level: Level? = null): MyScreen(game), ManuallyJsonSerializable, PostUpdatable {
    var tick = 0L
    protected val world = World(Vector2(0f, -30f), true)
    protected val objects = Group()
    protected val playerThinker = PlayerThinker(game)
    protected val justAddedObjects = mutableListOf<InGameObject>()
    protected val justDestroyedObjects = mutableSetOf<InGameObject>()
    val player = parseInGameObject(jsonSettings["tmp_player"])(game).also {
        if (it is Creature) {
            it.thinker = playerThinker
        }
    }

    init {
        level?.create(game)?.forEach { addObject(it) }
        addObject(player)
        stage.addActor(objects)
        screenActors.addActor(playerThinker)
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
        objects.children.map { it as InGameObject }
            .filter { !it.alive }
            .forEach { justDestroyedObjects.add(it) }

        justDestroyedObjects.forEach {
            objects.removeActor(it)
            it.dispose()
        }
        justDestroyedObjects.clear()
    }

    override fun update(delta: Float) {
        super.update(delta)
        world.step(delta, 10, 10)

        addObjects()
        destroyObjects()
    }

    override fun postUpdate(delta: Float) {
        objects.children.forEach { (it as InGameObject).postAct(delta) }

        centerCameraAt(player.body.getCenter())
        stage.camera.up.set(Vector2(0f, 1f).rotateDeg(player.rotation), 0f)
        tick++
        super.postUpdate(delta)
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
        json.addChild("tick", tick.toJsonValue())
    }

    override fun deserializeUpdates(json: JsonValue) {
        val updates = json["objects"].associateBy { it["id"].asInt() }
        objects.children.forEach { obj -> updates[obj.hashCode()]?.let { obj.updateFromJson(it) } }
        world.gravity = json["worldGravity", vector]
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

    open fun listenAction(action: ThinkerAction, creature: Creature) {
        // no operations
    }
}
