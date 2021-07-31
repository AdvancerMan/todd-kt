package com.company.todd.objects.passive.interactive

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Manifold
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.passive.platform.HalfCollidedPlatform
import com.company.todd.asset.texture.ToddDrawable
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.json.JsonFullSerializable
import com.company.todd.json.SerializationType

/**
 * Half collided platform that sets y velocity to [pushPower] on collision
 *
 * @param drawable Base drawable for InGameObject.
 *                 Z-index is relative to object's actor,
 *                 offset is relative to unrotated, unflipped actor position
 * @param bodyPattern Body description for InGameObject
 * @param pushPower Y velocity that would be set to another object on collision
 * @param scale Actor's scale
 */
@SerializationType(InGameObject::class, "Jumper")
class Jumper(
    game: ToddGame, drawable: ToddDrawable, bodyPattern: BodyPattern,
    @JsonFullSerializable private val pushPower: Float, scale: Float = 1f
) : HalfCollidedPlatform(game, drawable, bodyPattern, scale) {
    override fun processContact(other: InGameObject, contact: Contact, oldManifold: Manifold) {
        super.processContact(other, contact, oldManifold)
        other.body.setYVelocity(pushPower)
    }
}
