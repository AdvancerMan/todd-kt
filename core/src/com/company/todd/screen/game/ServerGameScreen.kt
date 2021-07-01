package com.company.todd.screen.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.creature.Player
import com.company.todd.objects.passive.Level
import com.company.todd.json.JsonUpdateSerializable
import com.company.todd.json.serialization.toJsonFull
import com.company.todd.json.serialization.toJsonUpdates
import com.company.todd.json.serialization.toJsonValue
import com.company.todd.net.ToddUDPServer
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.creature.Creature
import com.company.todd.thinker.operated.OperatedThinker
import com.company.todd.thinker.operated.ThinkerAction
import com.company.todd.util.synchronizedFlush
import java.net.SocketAddress

class ServerGameScreen(game: ToddGame, info: String, level: Level? = null): GameScreen(game, level), ToddUDPServer.ServerUpdatesListener {
    private var sinceLastSend = 0f
    private val server = ToddUDPServer(this, info.toByteArray())
    private var started = false
    private val incomingUpdates = mutableListOf<Pair<SocketAddress, ThinkerAction>>()
    private val connectedPlayers = mutableMapOf<SocketAddress, Pair<Player, OperatedThinker>>()
    private val updatedThinkerActions = mutableListOf<Action>()
    private val addedObjects = mutableListOf<InGameObject>()
    private val destroyedObjects = mutableListOf<InGameObject>()

    override fun addObjects() {
        justAddedObjects.forEach { addedObjects.add(it) }
        super.addObjects()
    }

    override fun destroyObjects() {
        justDestroyedObjects.forEach { destroyedObjects.add(it) }
        super.destroyObjects()
    }

    override fun receiveClientUpdates(socketAddress: SocketAddress, updates: String) {
        validatedAction(updates)?.let {
            synchronized(incomingUpdates) {
                incomingUpdates.add(socketAddress to it)
            }
        }
    }

    private fun validatedAction(action: String): ThinkerAction? {
        return ThinkerAction.values().find { it.name == action }
    }

    @Synchronized
    override fun getOnConnectInfo(socketAddress: SocketAddress): String {
        val thinker = OperatedThinker()
        val newPlayer = Player(game, thinker)
        val id = newPlayer.hashCode()

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
    override fun update(delta: Float) {
        val moment = System.currentTimeMillis()
        sinceLastSend += Gdx.graphics.rawDeltaTime

        incomingUpdates.synchronizedFlush()
            .mapNotNull { connectedPlayers[it.first]?.let { playerDescription -> playerDescription to it.second } }
            .onEach { updatedThinkerActions.add(Action(it.second, moment, it.first.first.hashCode())) }
            .forEach { it.first.second.addAction(it.second) }

        super.update(delta)

        if (!started) {
            server.start()
            started = true
        }

        if (sinceLastSend >= SEND_UPDATES_INTERVAL) {
            server.send(toJsonUpdates().toJson(JsonWriter.OutputType.json))
            updatedThinkerActions.clear()
            addedObjects.clear()
            destroyedObjects.clear()
            sinceLastSend = 0f
        }
    }

    override fun listenAction(action: ThinkerAction, creature: Creature) {
        updatedThinkerActions.add(Action(action, System.currentTimeMillis(), creature.id))
    }

    @Synchronized
    override fun dispose() {
        server.close()
        super.dispose()
    }

    override fun serializeUpdates(json: JsonValue) {
        super.serializeUpdates(json)
        json.addChild("sinceEpoch", System.currentTimeMillis().toJsonValue())
        json.addChild("actions", updatedThinkerActions.toJsonUpdates())

        val addedIds = addedObjects.map { it.hashCode() }.toSet()
        val jsonObjects = json["objects"]
        jsonObjects.removeAll { it["id"].asInt() in addedIds }
        addedObjects.forEach {
            val objectJson = it.toJsonFull()
            objectJson.addChild("meta", MetaMessage.ADDED.name.toJsonValue())
            jsonObjects.addChild(objectJson)
        }

        destroyedObjects.forEach { obj ->
            jsonObjects.addChild(JsonValue(JsonValue.ValueType.`object`).apply {
                addChild("meta", MetaMessage.DESTROYED.name.toJsonValue())
                addChild("id", obj.hashCode().toJsonValue())
            })
        }
    }

    override fun serializeFull(json: JsonValue) {
        super.serializeFull(json)
        json["objects"].forEach { it.addChild("meta", MetaMessage.ADDED.name.toJsonValue()) }
    }

    private data class Action(@JsonUpdateSerializable private val action: ThinkerAction,
                              @JsonUpdateSerializable private val sinceEpoch: Long,
                              @JsonUpdateSerializable private val id: Int)

    companion object {
        const val SEND_UPDATES_INTERVAL = 0.032f
    }

    enum class MetaMessage {
        ADDED, DESTROYED;
    }
}
