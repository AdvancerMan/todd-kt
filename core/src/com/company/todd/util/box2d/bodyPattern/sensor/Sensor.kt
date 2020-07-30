package com.company.todd.util.box2d.bodyPattern.sensor

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.Manifold
import com.company.todd.objects.base.InGameObject

interface Sensor {
    fun beginContact(other: InGameObject, contact: Contact) {}
    fun endContact(other: InGameObject, contact: Contact) {}
    fun preSolve(other: InGameObject, contact: Contact, oldManifold: Manifold) {}
    fun postSolve(other: InGameObject, contact: Contact, impulse: ContactImpulse) {}
}
