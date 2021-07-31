package com.company.todd.objects.passive.platform

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.Fixture
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.asset.texture.ToddDrawable
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.box2d.bodyPattern.sensor.Sensor
import com.company.todd.json.JsonFullSerializable
import com.company.todd.json.SerializationType

/**
 * Half collided platform that disappears for some time after collision
 *
 * @param drawable Base drawable for InGameObject.
 *                 Z-index is relative to object's actor,
 *                 offset is relative to unrotated, unflipped actor position
 * @param bodyPattern Body description for InGameObject
 * @param sinceContactTillInactive Time period between first contact
 *                                 with some object and disappearing
 * @param sinceInactiveTillActive Time period between disappearing and reappearing
 * @param scale Actor's scale
 */
@SerializationType(InGameObject::class, "cloudy")
class CloudyPlatform(
    game: ToddGame, drawable: ToddDrawable, bodyPattern: BodyPattern,
    @JsonFullSerializable private val sinceContactTillInactive: Float,
    sinceInactiveTillActive: Float, scale: Float = 1f
) : HalfCollidedPlatform(game, drawable, bodyPattern, scale) {
    private val sinceContactTillActive = sinceContactTillInactive + sinceInactiveTillActive
    private var sinceContact = sinceContactTillActive + 1

    override fun act(delta: Float) {
        sinceContact += delta
        super.act(delta)
    }

    override fun updateColor() {
        super.updateColor()

        if (sinceContact < sinceContactTillInactive) {
            color.a *= 1 - sinceContact / sinceContactTillInactive
        } else if (sinceContact < sinceContactTillActive) {
            color.a = 0f
        }
    }

    override fun postAct(delta: Float) {
        super.postAct(delta)
        body.setActive(sinceContact <= sinceContactTillInactive || sinceContact >= sinceContactTillActive)
    }

    override fun postSolve(otherSensor: Sensor, other: InGameObject, myFixture: Fixture,
                           otherFixture: Fixture, contact: Contact, impulse: ContactImpulse) {
        super.postSolve(otherSensor, other, myFixture, otherFixture, contact, impulse)
        if (otherSensor === other) {
            if (contact.isEnabled && sinceContact > sinceContactTillActive) {
                sinceContact = 0f
            }
        }
    }

    @JsonFullSerializable("sinceInactiveTillActive")
    private fun getSinceInactiveTillActive() = sinceContactTillActive - sinceContactTillInactive
}
