package com.company.todd.util.box2d.bodyPattern.sensor

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.Manifold
import com.company.todd.objects.base.InGameObject

interface Sensor {
    fun beginContact(otherSensor: Sensor, other: InGameObject, contact: Contact) {}
    fun endContact(otherSensor: Sensor, other: InGameObject, contact: Contact) {}
    fun preSolve(otherSensor: Sensor, other: InGameObject, contact: Contact, oldManifold: Manifold) {}
    fun postSolve(otherSensor: Sensor, other: InGameObject, contact: Contact, impulse: ContactImpulse) {}
}
