package com.company.todd.screen.game

import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import com.company.todd.json.JsonUpdateSerializable
import com.company.todd.json.deserialization.*
import com.company.todd.json.serialization.toJsonFull
import com.company.todd.launcher.ToddGame
import com.company.todd.net.ToddUDPClient
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.creature.Creature
import com.company.todd.thinker.operated.ClientThinker
import com.company.todd.thinker.operated.ThinkerAction
import com.company.todd.util.removeWhile
import java.util.*

class ClientGameScreen(game: ToddGame, private val client: ToddUDPClient, serverData: String) : GameScreen(game),
    ToddUDPClient.ClientUpdatesListener {
    private val reader = JsonReader()
    private var disconnected = false
    private val thinkers = mutableMapOf<Int, ClientThinker>()
    private val updateJustCreatedFromJson = mutableMapOf<InGameObject, JsonValue>()
    private val delayedPreUpdates: TreeSet<Pair<Long, () -> Unit>>
    private val delayedPostUpdates: TreeSet<Pair<Long, () -> Unit>>

    private val pingQueue: Queue<Long>
    private val ping: Long
        get() = pingQueue.average().toLong()
    val nowWithPing: Long
        get() = System.currentTimeMillis() - ping * 2

    private var updateStartMoment = System.currentTimeMillis()
    private var fromLastUpdate = 0L

    init {
        compareBy<Pair<Long, () -> Unit>> { it.first }.thenBy { it.second.toString() }.let {
            delayedPreUpdates = TreeSet(it)
            delayedPostUpdates = TreeSet(it)
        }

        val jsonData = reader.parse(serverData)
        val initialPing = jsonData["sinceEpoch", long]
        pingQueue = ArrayDeque(Collections.nCopies(60, initialPing))

        player.id = jsonData["playerId"].asInt()
        updateFromJson(jsonData)
        delayedPreUpdates.pollFirst()?.let {
            delayedPreUpdates.add(nowWithPing to it.second)
        }
    }

    override fun addObjects() {
        super.addObjects()
        updateJustCreatedFromJson.forEach { (igo, json) -> igo.updateFromJson(json) }
        updateJustCreatedFromJson.clear()
    }

    override fun whenConnected(serverData: String) {
        throw IllegalStateException("Should not happen")
    }

    @Synchronized
    override fun getServerUpdates(updates: String) {
        updateFromJson(reader.parse(updates))
    }

    @Synchronized
    override fun onDisconnect() {
        disconnected = true
    }

    @Synchronized
    override fun render(delta: Float) {
        super.render(delta)
    }

    override fun update(delta: Float) {
        fromLastUpdate = System.currentTimeMillis() - updateStartMoment
        updateStartMoment += fromLastUpdate

        val now = nowWithPing
        delayedPreUpdates.removeWhile { it.first <= now }.forEach { it.second() }
        super.update(delta)
        delayedPostUpdates.removeWhile { it.first <= now }.forEach { it.second() }
    }

    override fun listenAction(action: ThinkerAction, creature: Creature) {
        super.listenAction(action, creature)
        if (creature == player) {
            client.sendUpdate(Action(action, fromLastUpdate + 1).toJsonFull().toJson(JsonWriter.OutputType.json))
        }
    }

    @Synchronized
    override fun dispose() {
        client.close()
        super.dispose()
    }

    override fun deserializeUpdates(json: JsonValue) {
        val sinceEpoch = json["sinceEpoch", long]
        pingQueue.remove()
        pingQueue.add(System.currentTimeMillis() - sinceEpoch)

        delayedPreUpdates.add(sinceEpoch to {
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

            objects.children.forEach {
                it as InGameObject
                if (it.id in destroyed || it.id in addedIds && it != player) {
                    destroyObject(it)
                }
            }
            added.forEach { addedJson ->
                val id = addedJson["id", int]
                if (id == player.id) {
                    updateJustCreatedFromJson[player] = addedJson
                    return@forEach
                }

                addedJson.remove("thinker")
                val igo = parseInGameObject(addedJson)(game)
                updateJustCreatedFromJson[igo] = addedJson
                igo.id = id
                if (igo is Creature) {
                    thinkers[igo.id] = igo.thinker as ClientThinker
                }
                addObject(igo)
            }
        })
        delayedPostUpdates.add(sinceEpoch to { super.deserializeUpdates(json) })

        json["actions"].forEach { jsonAction ->
            val action = ServerGameScreen.Action().also { it.updateFromJson(jsonAction) }
            thinkers[action.id]?.addAction(action.sinceEpoch, action.action)
        }
    }

    data class Action(@JsonUpdateSerializable var action: ThinkerAction,
                      @JsonUpdateSerializable var duration: Long) {
        constructor() : this(ThinkerAction.RUN_RIGHT, 0)
    }
}
