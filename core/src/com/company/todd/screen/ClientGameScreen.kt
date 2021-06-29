package com.company.todd.screen

import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.json.deserialization.*
import com.company.todd.launcher.ToddGame
import com.company.todd.net.ToddUDPClient
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.creature.Creature
import com.company.todd.thinker.operated.ScheduledThinker
import com.company.todd.thinker.operated.ThinkerAction

class ClientGameScreen(game: ToddGame, private val client: ToddUDPClient, serverData: String) : GameScreen(game),
    ToddUDPClient.ClientUpdatesListener {
    private var sinceCreation = 0f
    private val reader = JsonReader()
    private var disconnected = false
    private val thinkers = mutableMapOf<Int, ScheduledThinker>()
    private val updateJustCreatedFromJson = mutableMapOf<InGameObject, JsonValue>()

    init {
        val jsonData = reader.parse(serverData)
        player.id = jsonData["playerId"].asInt()
        updateFromJson(jsonData)
        sinceCreation = jsonData["sinceCreation", float]
    }

    override fun addObjects() {
        super.addObjects()
        updateJustCreatedFromJson.forEach { (igo, json) -> igo.updateFromJson(json) }
        updateJustCreatedFromJson.clear()
    }

    override fun whenConnected(serverData: String) {
        throw IllegalStateException("Should not happen")
    }

    override fun getServerUpdates(updates: String) {
        synchronized(this) {
            updateFromJson(reader.parse(updates))
        }
    }

    override fun onDisconnect() {
        synchronized(this) {
            disconnected = true
        }
    }

    override fun update(delta: Float) {
        synchronized(this) {
            sinceCreation += delta
            super.update(delta)
        }
    }

    override fun listenAction(action: ThinkerAction, creature: Creature) {
        super.listenAction(action, creature)
        if (creature == player) {
            client.sendUpdate(action.name)
        }
    }

    override fun deserializeUpdates(json: JsonValue) {
        super.deserializeUpdates(json)
        val destroyed = mutableSetOf<Int>()
        val addedIds = mutableSetOf<Int>()
        val added = mutableListOf<JsonValue>()
        world.gravity = json["worldGravity", vector]

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

        objects.children.forEach {
            it as InGameObject
            if (it.id in destroyed || it.id in addedIds) {
                destroyObject(it)
            }
        }
        added.forEach { addedJson ->
            val id = addedJson["id", int]
            if (id == player.id) {
                return@forEach
            }

            addedJson.remove("thinker")
            val igo = parseInGameObject(addedJson)(game)
            updateJustCreatedFromJson[igo] = addedJson
            igo.id = id
            if (igo is Creature) {
                thinkers[igo.id] = igo.thinker as ScheduledThinker
                igo.thinker.moment = json["sinceCreation"].asFloat()
            }
            addObject(igo)
        }

        json["actions"].forEach { jsonAction ->
            thinkers[jsonAction["id", int]]
                ?.addAction(jsonAction["sinceCreation", float], ThinkerAction.valueOf(jsonAction["action", string]))
        }
    }
}
