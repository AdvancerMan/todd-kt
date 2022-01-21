package io.github.advancerman.todd.objects.passive.interactive

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.utils.Queue
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.objects.base.InGameObject
import io.github.advancerman.todd.objects.base.RealBodyWrapper
import io.github.advancerman.todd.objects.passive.PassiveObject
import io.github.advancerman.todd.asset.texture.ToddDrawable
import io.github.advancerman.todd.box2d.bodyPattern.base.BodyPattern
import io.github.advancerman.todd.box2d.bodyPattern.sensor.Sensor
import io.github.advancerman.todd.json.JsonFullSerializable
import io.github.advancerman.todd.json.SerializationType

/**
 * Portal that teleports objects to another location on collision
 *
 * @param drawable Base drawable for InGameObject.
 *                 Z-index is relative to object's actor,
 *                 offset is relative to unrotated, unflipped actor position
 * @param bodyPattern Body description for InGameObject
 * @param teleportTo Location where to teleport objects on collision
 * @param teleportDelay Time period between collision and actual teleportation
 * @param scale Actor's scale
 */
@SerializationType([InGameObject::class], "Portal")
class Portal(
    game: ToddGame, drawable: ToddDrawable, bodyPattern: BodyPattern,
    @JsonFullSerializable private val teleportTo: Vector2,
    @JsonFullSerializable private val teleportDelay: Float,
    scale: Float = 1f
) : PassiveObject(game, drawable, RealBodyWrapper(bodyPattern), scale) {
    private val delayedObjects = Queue<Pair<InGameObject, Float>>()
    private var timeSinceCreation = 0f

    override fun act(delta: Float) {
        timeSinceCreation += delta
        while (delayedObjects.notEmpty() && delayedObjects.first().second < timeSinceCreation) {
            val igo = delayedObjects.removeFirst().first
            if (igo.alive) {
                igo.body.setPosition(teleportTo.x, teleportTo.y, false)
            }
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
