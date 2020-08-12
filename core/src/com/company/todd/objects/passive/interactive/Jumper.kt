package com.company.todd.objects.passive.interactive

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Manifold
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.passive.platform.HalfCollidedPlatform
import com.company.todd.util.asset.texture.MyDrawable

class Jumper(game: ToddGame, drawable: MyDrawable, bodyPosition: Vector2,
             bodySize: Vector2, drawableSize: Vector2,
             bodyLowerLeftCornerOffset: Vector2, private val pushPower: Float) :
        HalfCollidedPlatform(game, drawable, bodyPosition, bodySize, drawableSize, bodyLowerLeftCornerOffset) {
    override fun processContact(other: InGameObject, contact: Contact, oldManifold: Manifold) {
        super.processContact(other, contact, oldManifold)
        other.setYVelocity(pushPower)
    }
}
