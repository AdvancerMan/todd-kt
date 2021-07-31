package com.company.todd.objects.passive.platform

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Manifold
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.base.toMeters
import com.company.todd.util.SPF
import com.company.todd.asset.texture.ToddDrawable
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.box2d.bodyPattern.sensor.Sensor
import com.company.todd.json.SerializationType
import com.company.todd.util.HALF_COL_GROUND_VEL_SCL
import com.company.todd.util.Y_VEL_JUMP_THRESHOLD

/**
 * Solid platform that does not register collision if object bottom side is under platform top side
 *
 * @param drawable Base drawable for InGameObject.
 *                 Z-index is relative to object's actor,
 *                 offset is relative to unrotated, unflipped actor position
 * @param bodyPattern Body description for InGameObject
 * @param scale Actor's scale
 */
@SerializationType(InGameObject::class, "halfCollided")
open class HalfCollidedPlatform(game: ToddGame, drawable: ToddDrawable, bodyPattern: BodyPattern, scale: Float = 1f) :
        SolidPlatform(game, drawable, bodyPattern, scale) {
    protected val groundFor = mutableMapOf<InGameObject, Int>()

    override fun beginOnGround(obj: InGameObject) {
        super.beginOnGround(obj)
        groundFor[obj] = groundFor.getOrElse(obj) { 0 } + 1
    }

    override fun endOnGround(obj: InGameObject) {
        super.endOnGround(obj)
        groundFor[obj]!!.let {
            if (it == 1) {
                groundFor.remove(obj)
            } else {
                groundFor[obj] = it - 1
            }
        }
    }

    override fun preSolve(otherSensor: Sensor, other: InGameObject, myFixture: Fixture,
                          otherFixture: Fixture, contact: Contact, oldManifold: Manifold) {
        super.preSolve(otherSensor, other, myFixture, otherFixture, contact, oldManifold)
        if (isGroundInContact(otherSensor, other, myFixture, otherFixture, contact, oldManifold)) {
            processContact(other, contact, oldManifold)
        } else {
            contact.isEnabled = false
        }
    }

    protected open fun isGroundInContact(otherSensor: Sensor, other: InGameObject, myFixture: Fixture,
                                         otherFixture: Fixture, contact: Contact, oldManifold: Manifold) =
            other.body.getVelocity().y <= Y_VEL_JUMP_THRESHOLD
                    && (groundFor.containsKey(other)
                    || contact.worldManifold.numberOfContactPoints == 2
                    && contact.worldManifold.points
                    .all { myFixture.testPoint(it) }
                    && contact.worldManifold.points
                    .map {
                        it.cpy().sub(
                            0f,
                            other.body.getVelocity().y.toMeters() * SPF * HALF_COL_GROUND_VEL_SCL
                        )
                    }
                    .all { !myFixture.testPoint(it) })

    protected open fun processContact(other: InGameObject, contact: Contact, oldManifold: Manifold) {}
}
