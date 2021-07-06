package com.company.todd.screen.game

import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import com.company.todd.json.JsonUpdateSerializable
import com.company.todd.json.deserialization.*
import com.company.todd.json.serialization.toJsonFull
import com.company.todd.json.serialization.toJsonUpdates
import com.company.todd.launcher.ToddGame
import com.company.todd.net.ToddUDPClient
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.creature.Creature
import com.company.todd.thinker.operated.ScheduledThinker
import com.company.todd.thinker.operated.ThinkerAction
import com.company.todd.util.removeWhile
import com.company.todd.util.synchronizedFlush
import kotlin.collections.ArrayDeque

class ClientGameScreen(
    game: ToddGame, private val client: ToddUDPClient,
    serverData: String, @Volatile private var ping: Long
) : GameScreen(game), ToddUDPClient.ClientUpdatesListener {
    @Volatile private var disconnected = false
    private val thinkers = mutableMapOf<Int, ScheduledThinker>()
    private val justCreatedToUpdateFromJson = mutableMapOf<InGameObject, JsonValue>()
    private val delayedUpdates = mutableMapOf<Long, Pair<() -> Unit, () -> Unit>>()
    private val playerInThePast = ArrayDeque<Pair<Long, JsonValue>>()
    private var newestPlayerUpdate: Pair<Long, JsonValue>? = null

    private val serverUpdates = mutableListOf<JsonValue>()

    private var shouldLagBack = false

    init {
        val jsonData = JsonReader().parse(serverData)
        tick = jsonData["tick", long] - 5
        player.id = jsonData["playerId"].asInt()

        deserializeUpdates(jsonData)
    }

    override fun addObjects() {
        super.addObjects()
        justCreatedToUpdateFromJson.forEach { (igo, json) -> igo.updateFromJson(json) }
        justCreatedToUpdateFromJson.clear()
    }

    override fun onNewPing(ping: Long) {
        this.ping = ping
    }

    override fun onConnection(serverData: String, ping: Long) {
        throw IllegalStateException("Should not happen")
    }

    override fun onServerUpdates(updates: String) {
        val parsed = JsonReader().parse(updates)
        synchronized(serverUpdates) {
            serverUpdates.add(parsed)
        }
    }

    override fun onDisconnect() {
        disconnected = true
    }

    override fun update(delta: Float) {
        serverUpdates.synchronizedFlush().forEach { deserializeUpdates(it) }

        delayedUpdates.keys.filter { it < tick }.forEach { delayedUpdates.remove(it) }
        val delayedUpdate = delayedUpdates.remove(tick)
        delayedUpdate?.first?.invoke()
        super.update(delta)
        delayedUpdate?.second?.invoke()

        if (shouldLagBack) {
            newestPlayerUpdate?.let {
                doLagBack(it.second, it.first)
                shouldLagBack = false
                newestPlayerUpdate = null
            }
        }
    }

    override fun shouldSkipFrame(): Boolean {
        // minus one because tick was incremented in postUpdate
        return (delayedUpdates.maxOfOrNull { it.key } ?: tick - 1) - (tick - 1) >= SKIP_FRAMES_THRESHOLD_TICKS
    }

    override fun listenAction(action: ThinkerAction, creature: Creature) {
        super.listenAction(action, creature)
        if (creature == player) {
            client.sendUpdate(Action(action).toJsonFull().toJson(JsonWriter.OutputType.json))
        }
    }

    override fun dispose() {
        client.close()
        super.dispose()
    }

    override fun deserializeUpdates(json: JsonValue) {
        val serverTick = json["tick", long]
        val jsonObjects = json["objects"]
        val playerJson = jsonObjects.indexOfFirst { it["id", int] == player.id }.let {
            if (it == -1) null else jsonObjects.remove(it)
        }
        if (playerJson != null && (newestPlayerUpdate == null || serverTick > newestPlayerUpdate!!.first)) {
            newestPlayerUpdate = serverTick to playerJson
        }

        delayedUpdates[serverTick] = {
            val destroyed = mutableSetOf<Int>()
            val addedIds = mutableSetOf<Int>()
            val added = mutableListOf<JsonValue>()

            json["objects"].forEach { objectJson ->
                objectJson["meta"]?.asString()?.let { meta ->
                    if (meta == ServerGameScreen.MetaMessage.DESTROYED.name) {
                        destroyed.add(objectJson["id", int])
                    } else if (meta == ServerGameScreen.MetaMessage.ADDED.name) {
                        addedIds.add(objectJson["id", int])
                        added.add(objectJson)
                    }
                }
            }
            json["objects"].removeAll { it["meta"]?.asString() == ServerGameScreen.MetaMessage.DESTROYED.name}

            deserializeDestruction(destroyed, addedIds)
            playerJson?.let { deserializePlayer(it) }
            deserializeAdded(added)
        } to {
            playerJson?.let { postDeserializePlayer(it) }
            super.deserializeUpdates(json)
        }

        json["actions"].forEach { jsonAction ->
            val action = ServerGameScreen.Action().also { it.updateFromJson(jsonAction) }
            thinkers[action.id]?.addAction(action.tick, action.action)
        }
    }

    private fun deserializeDestruction(destroyed: Set<Int>, added: Set<Int>) {
        objects.children.forEach {
            it as InGameObject
            if (it.id in destroyed || it.id in added) {
                destroyObject(it)
            }
        }
    }

    private fun deserializeAdded(added: List<JsonValue>) {
        added.forEach { addedJson ->
            val id = addedJson["id", int]
            addedJson.remove("thinker")
            val igo = parseInGameObject(addedJson)(game)
            justCreatedToUpdateFromJson[igo] = addedJson
            igo.id = id
            if (igo is Creature) {
                thinkers[igo.id] = igo.thinker as ScheduledThinker
            }
            addObject(igo)
        }
    }

    private fun deserializePlayer(serverJson: JsonValue) {
        when (serverJson["meta"]?.asString()) {
            ServerGameScreen.MetaMessage.ADDED.name -> {
                justCreatedToUpdateFromJson[player] = serverJson
                return
            }
            ServerGameScreen.MetaMessage.DESTROYED.name -> {
                destroyObject(player)
                return
            }
        }
    }

    private fun postDeserializePlayer(serverJson: JsonValue) {
        shouldLagBack = playerShouldLagBack(player.toJsonUpdates(), serverJson)
    }

    private fun playerShouldLagBack(clientJson: JsonValue, serverJson: JsonValue): Boolean {
        val clientPosition = clientJson["bodyPattern"]["b2d_position", vector]
        val serverPosition = serverJson["bodyPattern"]["b2d_position", vector]
        return !serverPosition.epsilonEquals(clientPosition, 1f)
    }

    private fun doLagBack(json: JsonValue, jsonTick: Long) {
        player.body.updateFromJson(json["bodyPattern"])

    }

    data class Action(@JsonUpdateSerializable var action: ThinkerAction) {
        constructor() : this(ThinkerAction.RUN_RIGHT)
    }

    companion object {
        const val SKIP_FRAMES_THRESHOLD_TICKS = 5
    }
}
