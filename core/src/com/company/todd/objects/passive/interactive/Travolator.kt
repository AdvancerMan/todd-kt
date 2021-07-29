package com.company.todd.objects.passive.interactive

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Manifold
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.passive.platform.HalfCollidedPlatform
import com.company.todd.asset.texture.ToddDrawable
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.json.JsonFullSerializable
import com.company.todd.json.SerializationType
import kotlin.math.abs

private val objectToTravolatorNegativeImpulse = mutableMapOf<InGameObject, Float>()
private val objectToTravolatorPositiveImpulse = mutableMapOf<InGameObject, Float>()

@SerializationType(InGameObject::class, "travolator")
class Travolator(
    game: ToddGame, drawable: ToddDrawable, bodyPattern: BodyPattern,
    @JsonFullSerializable private val pushPower: Float, scale: Float
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
