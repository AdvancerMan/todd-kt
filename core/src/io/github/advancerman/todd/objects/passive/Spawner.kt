package io.github.advancerman.todd.objects.passive

import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.asset.texture.ToddDrawable
import io.github.advancerman.todd.box2d.bodyPattern.base.BodyPattern
import io.github.advancerman.todd.json.JsonFullSerializable
import io.github.advancerman.todd.json.JsonUpdateSerializable
import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.json.deserialization.parseInGameObject
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.objects.base.InGameObject
import io.github.advancerman.todd.objects.passive.platform.SolidPlatform

/**
 * Solid platform that can spawn another objects
 *
 * @param drawable Base drawable for InGameObject.
 *                 Z-index is relative to object's actor,
 *                 offset is relative to unrotated, unflipped actor position
 * @param bodyPattern Body description for InGameObject
 * @param igoPattern Json pattern of [InGameObject] to spawn
 * @param maxAmount Maximal amount of non-dead creatures spawned by this spawner
 * @param spawnPeriod Amount of time between spawns
 * @param scale Actor's scale
 */
@SerializationType(InGameObject::class, "Spawner")
class Spawner(
    game: ToddGame, drawable: ToddDrawable, bodyPattern: BodyPattern,
    @JsonFullSerializable private val igoPattern: JsonValue,
    @JsonFullSerializable private val maxAmount: Int,
    @JsonFullSerializable private val spawnPeriod: Float,
    scale: Float = 1f
) : SolidPlatform(game, drawable, bodyPattern, scale) {
    @JsonUpdateSerializable
    private var sinceSpawn = spawnPeriod

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
