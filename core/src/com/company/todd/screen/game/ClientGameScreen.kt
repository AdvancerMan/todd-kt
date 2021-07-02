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
import com.company.todd.thinker.operated.ClientThinker
import com.company.todd.thinker.operated.ThinkerAction
import com.company.todd.util.removeWhile
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.abs

class ClientGameScreen(game: ToddGame, private val client: ToddUDPClient, serverData: String) : GameScreen(game),
    ToddUDPClient.ClientUpdatesListener {
    private val reader = JsonReader()
    private var disconnected = false
    private val thinkers = mutableMapOf<Int, ClientThinker>()
    private val updateJustCreatedFromJson = mutableMapOf<InGameObject, JsonValue>()
    private val delayedPreUpdates: TreeSet<Pair<Long, () -> Unit>>
    private val delayedPostUpdates: TreeSet<Pair<Long, () -> Unit>>
    private val playerInThePast = ArrayDeque<Pair<Long, JsonValue>>()
    private var newestPlayerUpdate: Pair<Long, JsonValue>? = null

    private val pingQueue: ArrayDeque<Long>
    private val ping: Long
        get() = pingQueue.average().toLong()
    private val nowWithPing: Long
        get() = System.currentTimeMillis() - ping * 2
    var updateStartMomentWithPing: Long

    private var updateStartMoment = System.currentTimeMillis()
    private var fromLastUpdate = 0L

    private var shouldLagBack = false

    init {
        compareBy<Pair<Long, () -> Unit>> { it.first }.thenBy { it.second.toString() }.let {
            delayedPreUpdates = TreeSet(it)
            delayedPostUpdates = TreeSet(it)
        }

        val jsonData = reader.parse(serverData)
        val initialPing = System.currentTimeMillis() - jsonData["sinceEpoch", long]
        pingQueue = ArrayDeque(Collections.nCopies(60, initialPing))
        updateStartMomentWithPing = nowWithPing

        player.id = jsonData["playerId"].asInt()
        updateFromJson(jsonData)
        delayedPreUpdates.pollFirst()?.let { delayedPreUpdates.add(updateStartMomentWithPing to it.second) }
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
        updateStartMomentWithPing = nowWithPing

        delayedPreUpdates.removeWhile { it.first <= updateStartMomentWithPing }.forEach { it.second() }
        super.update(delta)
        delayedPostUpdates.removeWhile { it.first <= updateStartMomentWithPing }.forEach { it.second() }

        if (shouldLagBack) {
            newestPlayerUpdate?.let {
                doLagBack(it.second)
                shouldLagBack = false
                playerInThePast.clear()
                newestPlayerUpdate = null
            }
        }
        playerInThePast.add(updateStartMoment to player.toJsonUpdates())
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
        pingQueue.removeFirst()
        pingQueue.addLast(System.currentTimeMillis() - sinceEpoch)

        val jsonObjects = json["objects"]
        val playerJson = jsonObjects.indexOfFirst { it["id", int] == player.id }.let {
            if (it == -1) null else jsonObjects.remove(it)
        }
        if (playerJson != null && (newestPlayerUpdate == null || sinceEpoch > newestPlayerUpdate!!.first)) {
            newestPlayerUpdate = sinceEpoch to playerJson
        }

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

            deserializeDestruction(destroyed, addedIds)
            deserializeAdded(added)
            deserializePlayer(playerJson, sinceEpoch)
        })
        delayedPostUpdates.add(sinceEpoch to { super.deserializeUpdates(json) })

        json["actions"].forEach { jsonAction ->
            val action = ServerGameScreen.Action().also { it.updateFromJson(jsonAction) }
            thinkers[action.id]?.addAction(action.sinceEpoch, action.action)
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
            updateJustCreatedFromJson[igo] = addedJson
            igo.id = id
            if (igo is Creature) {
                thinkers[igo.id] = igo.thinker as ClientThinker
            }
            addObject(igo)
        }
    }

    private fun deserializePlayer(playerJson: JsonValue?, jsonMoment: Long) {
        playerJson?.let { serverJson ->
            when (serverJson["meta"]?.asString()) {
                ServerGameScreen.MetaMessage.ADDED.name -> {
                    updateJustCreatedFromJson[player] = serverJson
                    return@let
                }
                ServerGameScreen.MetaMessage.DESTROYED.name -> {
                    destroyObject(player)
                    return@let
                }
            }

            while (playerInThePast.size > 1 && playerInThePast[1].first <= jsonMoment) {
                playerInThePast.removeFirst()
            }
            playerInThePast.take(2)
                .minByOrNull { abs(it.first - jsonMoment) }
                ?.let { (t, clientJson) ->
                    shouldLagBack = if (t < jsonMoment) {
                        playerShouldLagBack(clientJson, serverJson, jsonMoment - t)
                    } else {
                        playerShouldLagBack(serverJson, clientJson, t - jsonMoment)
                    }
                }
        }
    }

    private fun playerShouldLagBack(jsonBefore: JsonValue, jsonAfter: JsonValue, delta: Long): Boolean {
        val positionBefore = jsonBefore["bodyPattern"]["b2d_position", vector]
        val positionAfter = jsonAfter["bodyPattern"]["b2d_position", vector]
        val velocityBefore = jsonBefore["bodyPattern"]["b2d_linearVelocity", vector]

        val positionDelta = positionBefore.mulAdd(velocityBefore, delta / 1000f).sub(positionAfter)
        return 1000f * 1000f * positionDelta.len2() >= velocityBefore.len2() * lagBackDelayMs * lagBackDelayMs
    }

    private fun doLagBack(json: JsonValue) {
        player.body.updateFromJson(json["bodyPattern"])
    }

    data class Action(@JsonUpdateSerializable var action: ThinkerAction,
                      @JsonUpdateSerializable var duration: Long) {
        constructor() : this(ThinkerAction.RUN_RIGHT, 0)
    }

    companion object {
        const val lagBackDelayMs = 100f
    }
}
