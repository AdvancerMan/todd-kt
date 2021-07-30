package com.company.todd.objects.passive.interactive

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Manifold
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.passive.platform.HalfCollidedPlatform
import com.company.todd.asset.texture.ToddDrawable
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.json.SerializationType

@SerializationType(InGameObject::class, "trampoline")
class Trampoline(game: ToddGame, drawable: ToddDrawable, bodyPattern: BodyPattern, scale: Float = 1f) :
        HalfCollidedPlatform(game, drawable, bodyPattern, scale) {
    override fun processContact(other: InGameObject, contact: Contact, oldManifold: Manifold) {
        super.processContact(other, contact, oldManifold)
        contact.restitution = 1f
    }
}
