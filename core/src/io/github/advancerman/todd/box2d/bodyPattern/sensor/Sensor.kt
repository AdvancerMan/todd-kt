package io.github.advancerman.todd.box2d.bodyPattern.sensor

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Manifold
import io.github.advancerman.todd.objects.base.InGameObject

interface Sensor {
    fun beginContact(otherSensor: Sensor, other: InGameObject, myFixture: Fixture,
                     otherFixture: Fixture, contact: Contact) {}

    fun endContact(otherSensor: Sensor, other: InGameObject, myFixture: Fixture,
                   otherFixture: Fixture, contact: Contact) {}

    fun preSolve(otherSensor: Sensor, other: InGameObject, myFixture: Fixture,
                 otherFixture: Fixture, contact: Contact, oldManifold: Manifold) {}

    fun postSolve(otherSensor: Sensor, other: InGameObject, myFixture: Fixture,
                  otherFixture: Fixture, contact: Contact, impulse: ContactImpulse) {}
}
