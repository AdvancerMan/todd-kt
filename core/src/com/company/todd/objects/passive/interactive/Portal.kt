package com.company.todd.objects.passive.interactive

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.utils.Queue
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.objects.passive.PassiveObject
import com.company.todd.util.asset.texture.MyDrawable
import com.company.todd.util.box2d.bodyPattern.base.CircleBodyPattern

class Portal(game: ToddGame, drawable: MyDrawable, position: Vector2, radius: Float,
             private val teleportTo: Vector2, private val teleportDelay: Float) :
        PassiveObject(game, drawable, RealBodyWrapper(CircleBodyPattern(BodyDef.BodyType.StaticBody, radius, position))) {
    private val delayedObjects = Queue<Pair<InGameObject, Float>>()
    private var timeSinceCreation = 0f

    override fun isGroundFor(other: InGameObject) = false

    override fun act(delta: Float) {
        timeSinceCreation += delta
        while (delayedObjects.notEmpty() && delayedObjects.first().second < timeSinceCreation) {
            delayedObjects.removeFirst().first.setPosition(teleportTo.x, teleportTo.y, false)
        }
        super.act(delta)
    }

    override fun beginContact(other: InGameObject, contact: Contact) {
        super.beginContact(other, contact)
        delayedObjects.addLast(other to timeSinceCreation + teleportDelay)
    }

    override fun preSolve(other: InGameObject, contact: Contact, oldManifold: Manifold) {
        super.preSolve(other, contact, oldManifold)
        contact.isEnabled = false
    }
}
