package io.github.advancerman.todd.objects.passive.interactive

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Manifold
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.objects.base.InGameObject
import io.github.advancerman.todd.objects.passive.platform.HalfCollidedPlatform
import io.github.advancerman.todd.asset.texture.ToddDrawable
import io.github.advancerman.todd.box2d.bodyPattern.base.BodyPattern
import io.github.advancerman.todd.json.JsonFullSerializable
import io.github.advancerman.todd.json.SerializationType
import kotlin.math.abs

private val objectToTravolatorNegativeImpulse = mutableMapOf<InGameObject, Float>()
private val objectToTravolatorPositiveImpulse = mutableMapOf<InGameObject, Float>()

/**
 * Half collided platform that applies impulse of [pushPower] power to another object on collision
 *
 * @param drawable Base drawable for InGameObject.
 *                 Z-index is relative to object's actor,
 *                 offset is relative to unrotated, unflipped actor position
 * @param bodyPattern Body description for InGameObject
 * @param pushPower Impulse power to apply on collision
 * @param scale Actor's scale
 */
@SerializationType([InGameObject::class], "Travolator")
class Travolator(
    game: ToddGame, drawable: ToddDrawable, bodyPattern: BodyPattern,
    @JsonFullSerializable private val pushPower: Float, scale: Float = 1f
) : HalfCollidedPlatform(game, drawable, bodyPattern, scale) {
    override fun processContact(other: InGameObject, contact: Contact, oldManifold: Manifold) {
        super.processContact(other, contact, oldManifold)
        val impulseMap = if (pushPower > 0) objectToTravolatorPositiveImpulse else objectToTravolatorNegativeImpulse
        val impulse = impulseMap[other] ?: 0f
        if (abs(impulse) < abs(pushPower)) {
            other.body.applyLinearImpulseToCenter(Vector2(pushPower - impulse, 0f))
            impulseMap[other] = pushPower
        }
    }

    override fun postAct(delta: Float) {
        super.postAct(delta)
        objectToTravolatorNegativeImpulse.clear()
        objectToTravolatorPositiveImpulse.clear()
    }
}
