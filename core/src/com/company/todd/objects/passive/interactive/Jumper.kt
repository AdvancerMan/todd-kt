package com.company.todd.objects.passive.interactive

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Manifold
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.passive.platform.HalfCollidedPlatform
import com.company.todd.util.asset.texture.MyDrawable

class Jumper(game: ToddGame, drawable: MyDrawable,
             aabb: Rectangle, drawableSize: Vector2,
             bodyLowerLeftCornerOffset: Vector2, private val pushPower: Float) :
        HalfCollidedPlatform(game, drawable, aabb, drawableSize, bodyLowerLeftCornerOffset) {
    override fun processContact(other: InGameObject, contact: Contact, oldManifold: Manifold) {
        super.processContact(other, contact, oldManifold)
        other.setYVelocity(pushPower)
    }
}
