package com.company.todd.objects.passive.interactive

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.utils.Queue
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.objects.passive.PassiveObject
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.box2d.bodyPattern.base.CircleBodyPattern
import com.company.todd.box2d.bodyPattern.sensor.Sensor
import com.company.todd.json.serialization.SerializationType

@SerializationType("portal")
class Portal(game: ToddGame, drawable: MyDrawable, drawableSize: Vector2,
             bodyLowerLeftCornerOffset: Vector2, center: Vector2, radius: Float,
             private val teleportTo: Vector2, private val teleportDelay: Float) :
        PassiveObject(
                game, drawable, drawableSize, bodyLowerLeftCornerOffset,
                RealBodyWrapper(CircleBodyPattern(BodyDef.BodyType.StaticBody, center, radius))
        ) {
    private val delayedObjects = Queue<Pair<InGameObject, Float>>()
    private var timeSinceCreation = 0f

    override fun act(delta: Float) {
        timeSinceCreation += delta
        while (delayedObjects.notEmpty() && delayedObjects.first().second < timeSinceCreation) {
            delayedObjects.removeFirst().first.setPosition(teleportTo.x, teleportTo.y, false)
        }
        super.act(delta)
    }

    override fun beginContact(otherSensor: Sensor, other: InGameObject, myFixture: Fixture,
                              otherFixture: Fixture, contact: Contact) {
        super.beginContact(otherSensor, other, myFixture, otherFixture, contact)
        if (otherSensor === other) {
            delayedObjects.addLast(other to timeSinceCreation + teleportDelay)
        }
    }

    override fun preSolve(otherSensor: Sensor, other: InGameObject, myFixture: Fixture,
                          otherFixture: Fixture, contact: Contact, oldManifold: Manifold) {
        super.preSolve(otherSensor, other, myFixture, otherFixture, contact, oldManifold)
        contact.isEnabled = false
    }
}
