package com.company.todd.util.box2d

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.company.todd.objects.base.InGameObject
import com.company.todd.util.box2d.bodyPattern.sensor.Sensor

private fun invokeForBoth(contact: Contact, f: Sensor.(Sensor, InGameObject) -> Unit) {
    val a = contact.fixtureA.body.userData as InGameObject
    val b = contact.fixtureB.body.userData as InGameObject

    val aSensor = if (contact.fixtureA.isSensor) contact.fixtureA.userData as Sensor else a
    val bSensor = if (contact.fixtureB.isSensor) contact.fixtureB.userData as Sensor else b

    aSensor.f(bSensor, b)
    bSensor.f(aSensor, a)
}

class MyContactListener: ContactListener {
    override fun beginContact(contact: Contact) =
            invokeForBoth(contact) { sensor, obj -> beginContact(sensor, obj, contact) }

    override fun endContact(contact: Contact) =
            invokeForBoth(contact) { sensor, obj -> endContact(sensor, obj, contact) }

    override fun preSolve(contact: Contact, oldManifold: Manifold) =
            invokeForBoth(contact) { sensor, obj -> preSolve(sensor, obj, contact, oldManifold) }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) =
            invokeForBoth(contact) { sensor, obj -> postSolve(sensor, obj, contact, impulse) }
}
