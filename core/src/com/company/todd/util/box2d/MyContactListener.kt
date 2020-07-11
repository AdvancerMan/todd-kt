package com.company.todd.util.box2d

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.company.todd.objects.base.InGameObject

private fun invokeForBoth(contact: Contact, f: InGameObject.(InGameObject) -> Unit) {
    contact.fixtureA.body.userData.let { a ->
        a as InGameObject
        contact.fixtureB.body.userData.let { b ->
            b as InGameObject

            a.f(b)
            b.f(a)
        }
    }
}

class MyContactListener: ContactListener {
    override fun beginContact(contact: Contact) =
            invokeForBoth(contact) { beginContact(it, contact) }

    override fun endContact(contact: Contact) =
            invokeForBoth(contact) { endContact(it, contact) }

    override fun preSolve(contact: Contact, oldManifold: Manifold) =
            invokeForBoth(contact) { preSolve(it, contact, oldManifold) }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) =
            invokeForBoth(contact) { postSolve(it, contact, impulse) }
}
