package io.github.advancerman.todd.screen.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import com.badlogic.gdx.utils.SerializationException
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.objects.passive.Level
import io.github.advancerman.todd.json.JsonUpdateSerializable
import io.github.advancerman.todd.json.deserialization.jsonSettings
import io.github.advancerman.todd.json.deserialization.parseInGameObject
import io.github.advancerman.todd.json.deserialization.updateFromJson
import io.github.advancerman.todd.json.serialization.toJsonFull
import io.github.advancerman.todd.json.serialization.toJsonUpdates
import io.github.advancerman.todd.json.serialization.toJsonValue
import io.github.advancerman.todd.net.ToddUDPServer
import io.github.advancerman.todd.objects.base.InGameObject
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.thinker.operated.ScheduledThinker
import io.github.advancerman.todd.thinker.operated.ThinkerAction
import io.github.advancerman.todd.util.synchronizedFlush
import java.net.SocketAddress

class ServerGameScreen(game: ToddGame, info: String, level: Level? = null): GameScreen(game, level), ToddUDPServer.ServerUpdatesListener {
    private var sinceLastSend = 0
    private val server = ToddUDPServer(this, info.toByteArray())
    private var started = false
    private val incomingUpdates = mutableListOf<Pair<SocketAddress, ClientGameScreen.Action>>()
    private val connectedPlayers = mutableMapOf<SocketAddress, Pair<Creature, ScheduledThinker>>()

    private val actionsToSend = mutableListOf<Action>()
    private val addedObjectsToSend = mutableListOf<InGameObject>()
    private val destroyedObjectsToSend = mutableListOf<InGameObject>()

    override fun addObjects() {
        justAddedObjects.forEach { addedObjectsToSend.add(it) }
        super.addObjects()
    }

    override fun destroyObjects() {
        justDestroyedObjects.forEach { destroyedObjectsToSend.add(it) }
        super.destroyObjects()
    }

    override fun receiveClientUpdates(socketAddress: SocketAddress, updates: String) {
        validatedAction(updates)?.let {
            synchronized(incomingUpdates) {
                incomingUpdates.add(socketAddress to it)
            }
        }
    }

    private fun validatedAction(message: String): ClientGameScreen.Action? {
        return try {
            ClientGameScreen.Action().also { it.updateFromJson(JsonReader().parse(message)) }
        } catch (e: SerializationException) {
            null
        }
    }

    @Synchronized
    override fun getOnConnectInfo(socketAddress: SocketAddress): String {
        val thinker = ScheduledThinker()
        val newPlayer = parseInGameObject(jsonSettings["tmp_player"])(game) as Creature
        newPlayer.thinker = thinker
        val id = newPlayer.id

        addObject(newPlayer)
        connectedPlayers[socketAddress] = newPlayer to thinker

        val json = toJsonFull()
        json.addChild("playerId", id.toJsonValue())
        return json.toJson(JsonWriter.OutputType.json)
    }

    @Synchronized
    override fun onDisconnect(socketAddress: SocketAddress) {
        connectedPlayers.remove(socketAddress)?.let {
            destroyObject(it.first)
        } ?: Gdx.app.error("ServerGameScreen", "Disconnect for non-existing player $socketAddress")
    }

    @Synchronized
    override fun render(delta: Float) {
        super.render(delta)
    }

    override fun update(delta: Float) {
        incomingUpdates.synchronizedFlush()
            .forEach { (address, action) -> connectedPlayers[address]?.second?.addActionAtLeastAt(tick, action.action) }

        super.update(delta)

        if (!started) {
            server.start(game)
            started = true
        }

        if (++sinceLastSend >= SEND_UPDATES_TICKS_INTERVAL) {
            server.send(toJsonUpdates().toJson(JsonWriter.OutputType.json))
            actionsToSend.clear()
            addedObjectsToSend.clear()
            destroyedObjectsToSend.clear()
            sinceLastSend = 0
        }
    }

    override fun listenAction(action: ThinkerAction, creature: Creature) {
        actionsToSend.add(Action(action, tick, creature.id))
    }

    @Synchronized
    override fun dispose() {
        server.close()
        super.dispose()
    }

    override fun serializeUpdates(json: JsonValue) {
        super.serializeUpdates(json)
        json.addChild("actions", actionsToSend.toJsonUpdates())

        val addedIds = addedObjectsToSend.map { it.id }.toSet()
        val jsonObjects = json["objects"]
        jsonObjects.removeAll { it["id"].asInt() in addedIds }
        addedObjectsToSend.forEach {
            val objectJson = it.toJsonFull()
            objectJson.addChild("meta", MetaMessage.ADDED.name.toJsonValue())
            jsonObjects.addChild(objectJson)
        }

        destroyedObjectsToSend.forEach { obj ->
            jsonObjects.addChild(JsonValue(JsonValue.ValueType.`object`).apply {
                addChild("meta", MetaMessage.DESTROYED.name.toJsonValue())
                addChild("id", obj.id.toJsonValue())
            })
        }
    }

    override fun serializeFull(json: JsonValue) {
        super.serializeFull(json)
        json["objects"].forEach { it.addChild("meta", MetaMessage.ADDED.name.toJsonValue()) }
    }

    data class Action(@JsonUpdateSerializable var action: ThinkerAction,
                      @JsonUpdateSerializable var tick: Long,
                      @JsonUpdateSerializable var id: Int) {
        constructor() : this(ThinkerAction.MOVE_RIGHT, 0, 0)
    }

    companion object {
        const val SEND_UPDATES_TICKS_INTERVAL = 2
    }

    enum class MetaMessage {
        ADDED, DESTROYED;
    }
}
