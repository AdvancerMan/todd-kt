package com.company.todd.objects.passive.platform

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Manifold
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.yVelJumpThreshold
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.base.toMeters
import com.company.todd.util.SPF
import com.company.todd.util.asset.texture.MyDrawable
import com.company.todd.util.box2d.bodyPattern.sensor.Sensor

const val halfColVelocityGroundCoef = 2f

open class HalfCollidedPlatform(game: ToddGame, drawable: MyDrawable, aabb: Rectangle) :
        SolidRectanglePlatform(game, drawable, aabb) {
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
            other.getVelocity().y <= yVelJumpThreshold
                    && (groundFor.containsKey(other)
                    || contact.worldManifold.numberOfContactPoints == 2
                    && contact.worldManifold.points
                    .all { myFixture.testPoint(it) }
                    && contact.worldManifold.points
                    .map { it.cpy().sub(0f, other.getVelocity().y.toMeters() * SPF * halfColVelocityGroundCoef) }
                    .all { !myFixture.testPoint(it) })

    protected open fun processContact(other: InGameObject, contact: Contact, oldManifold: Manifold) {}
}
