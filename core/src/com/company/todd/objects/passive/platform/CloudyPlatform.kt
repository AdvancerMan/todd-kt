package com.company.todd.objects.passive.platform

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.Fixture
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.util.asset.texture.MyDrawable
import com.company.todd.util.box2d.bodyPattern.sensor.Sensor

class CloudyPlatform(game: ToddGame, drawable: MyDrawable, aabb: Rectangle,
                     drawableSize: Vector2, bodyLowerLeftCornerOffset: Vector2,
                     private val sinceContactTillInactive: Float,
                     sinceInactiveTillActive: Float) :
        HalfCollidedPlatform(game, drawable, aabb, drawableSize, bodyLowerLeftCornerOffset) {
    private val sinceContactTillActive = sinceContactTillInactive + sinceInactiveTillActive
    private var sinceContact = sinceContactTillActive + 1

    override fun act(delta: Float) {
        sinceContact += delta

        if (sinceContact < sinceContactTillInactive) {
            color.a = 1 - sinceContact / sinceContactTillInactive
        } else {
            setActive(sinceContact >= sinceContactTillActive)
            color.a = if (sinceContact >= sinceContactTillActive) 1f else 0f
        }

        super.act(delta)
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
}
