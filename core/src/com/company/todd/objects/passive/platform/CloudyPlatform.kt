package com.company.todd.objects.passive.platform

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.util.asset.texture.drawable.MyDrawable

class CloudyPlatform(game: ToddGame, drawable: MyDrawable, aabb: Rectangle,
                     private val sinceContactTillInactive: Float,
                     sinceInactiveTillActive: Float) :
        HalfCollidedPlatform(game, drawable, aabb) {
    private val sinceContactTillActive = sinceContactTillInactive + sinceInactiveTillActive
    private var sinceContact = sinceContactTillActive + 1

    override fun act(delta: Float) {
        sinceContact += delta

        if (sinceContact < sinceContactTillInactive) {
            color = color.apply { a = 1 - sinceContact / sinceContactTillInactive }
        } else {
            setActive(sinceContact >= sinceContactTillActive)
            color = color.apply { a = if (sinceContact >= sinceContactTillActive) 1f else 0f }
        }

        super.act(delta)
    }

    override fun postSolve(other: InGameObject, contact: Contact, impulse: ContactImpulse) {
        super.postSolve(other, contact, impulse)
        if (contact.isEnabled && sinceContact > sinceContactTillActive) {
            sinceContact = 0f
        }
    }
}
