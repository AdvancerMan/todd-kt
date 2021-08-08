package io.github.advancerman.todd.box2d

import com.badlogic.gdx.physics.box2d.*
import io.github.advancerman.todd.objects.base.InGameObject
import io.github.advancerman.todd.box2d.bodyPattern.sensor.Sensor

private fun invokeForBoth(contact: Contact, f: Sensor.(Sensor, InGameObject, Fixture, Fixture) -> Unit) {
    val a = contact.fixtureA.body.userData as InGameObject
    val b = contact.fixtureB.body.userData as InGameObject

    val aSensor = if (contact.fixtureA.isSensor) contact.fixtureA.userData as Sensor else a
    val bSensor = if (contact.fixtureB.isSensor) contact.fixtureB.userData as Sensor else b

    aSensor.f(bSensor, b, contact.fixtureA, contact.fixtureB)
    bSensor.f(aSensor, a, contact.fixtureB, contact.fixtureA)
}

class MyContactListener: ContactListener {
    override fun beginContact(contact: Contact) =
            invokeForBoth(contact) { sensor, obj, myF, otherF -> beginContact(sensor, obj, myF, otherF, contact) }

    override fun endContact(contact: Contact) =
            invokeForBoth(contact) { sensor, obj, myF, otherF -> endContact(sensor, obj, myF, otherF, contact) }

    override fun preSolve(contact: Contact, oldManifold: Manifold) =
            invokeForBoth(contact) { sensor, obj, myF, otherF -> preSolve(sensor, obj, myF, otherF, contact, oldManifold) }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) =
            invokeForBoth(contact) { sensor, obj, myF, otherF -> postSolve(sensor, obj, myF, otherF, contact, impulse) }
}
