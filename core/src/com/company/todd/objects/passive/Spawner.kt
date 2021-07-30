package com.company.todd.objects.passive

import com.badlogic.gdx.utils.JsonValue
import com.company.todd.asset.texture.ToddDrawable
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.json.JsonFullSerializable
import com.company.todd.json.JsonUpdateSerializable
import com.company.todd.json.SerializationType
import com.company.todd.json.deserialization.parseInGameObject
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.base.RealBodyWrapper

@SerializationType(InGameObject::class, "spawner")
class Spawner(
    game: ToddGame, drawable: ToddDrawable, bodyPattern: BodyPattern,
    @JsonFullSerializable private val igoPattern: JsonValue,
    @JsonFullSerializable private val maxAmount: Int,
    @JsonFullSerializable private val spawnPeriod: Float,
    scale: Float = 1f
) : PassiveObject(game, drawable, RealBodyWrapper(bodyPattern), scale) {
    @JsonUpdateSerializable
    private var sinceSpawn = 0f

    @JsonUpdateSerializable
    private var spawned = mutableListOf<InGameObject>()

    override fun act(delta: Float) {
        super.act(delta)
        sinceSpawn += delta
        spawned.removeAll { !it.alive }
        if (sinceSpawn >= spawnPeriod && spawned.size < maxAmount) {
            val igo = parseInGameObject(igoPattern)(game)
            screen.addObject(igo)
            spawned.add(igo)
            sinceSpawn = 0f
        }
    }
}
