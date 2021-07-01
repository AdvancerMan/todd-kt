package com.company.todd.screen.game

import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.json.deserialization.*
import com.company.todd.launcher.ToddGame
import com.company.todd.net.ToddUDPClient
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.creature.Creature
import com.company.todd.thinker.operated.ScheduledThinker
import com.company.todd.thinker.operated.ThinkerAction
import com.company.todd.util.removeWhile
import java.util.*

class ClientGameScreen(game: ToddGame, private val client: ToddUDPClient, serverData: String) : GameScreen(game),
    ToddUDPClient.ClientUpdatesListener {
    private val reader = JsonReader()
    private var disconnected = false
    private val thinkers = mutableMapOf<Int, ScheduledThinker>()
    private val updateJustCreatedFromJson = mutableMapOf<InGameObject, JsonValue>()
    private val delayedUpdates = TreeSet(compareBy<Pair<Long, () -> Unit>> { it.first }.thenBy { it.second.toString() })
    private val pingQueue: Queue<Long>
    private val ping: Long
        get() = pingQueue.average().toLong()
    val nowWithPing: Long
        get() = System.currentTimeMillis() - ping * 2

    init {
        val jsonData = reader.parse(serverData)
        val initialPing = jsonData["sinceEpoch", long]
        pingQueue = ArrayDeque(Collections.nCopies(60, initialPing))

        player.id = jsonData["playerId"].asInt()
        updateFromJson(jsonData)
        delayedUpdates.pollFirst()?.let {
            delayedUpdates.add(nowWithPing to it.second)
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
    override fun update(delta: Float) {
        val now = nowWithPing
        delayedUpdates.removeWhile { it.first <= now }.forEach { it.second() }
        super.update(delta)
    }

    override fun listenAction(action: ThinkerAction, creature: Creature) {
        super.listenAction(action, creature)
        if (creature == player) {
            client.sendUpdate(action.name)
        }
    }

    override fun dispose() {
        client.close()
        super.dispose()
    }

    override fun deserializeUpdates(json: JsonValue) {
        val sinceEpoch = json["sinceEpoch", long]
        pingQueue.remove()
        pingQueue.add(System.currentTimeMillis() - sinceEpoch)

        delayedUpdates.add(sinceEpoch to {
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
                    thinkers[igo.id] = igo.thinker as ScheduledThinker
                }
                addObject(igo)
            }
            super.deserializeUpdates(json)
        })

        json["actions"].forEach { jsonAction ->
            thinkers[jsonAction["id", int]]
                ?.addAction(jsonAction["sinceEpoch", long], ThinkerAction.valueOf(jsonAction["action", string]))
        }
    }
}
