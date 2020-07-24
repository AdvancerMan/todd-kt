package com.company.todd.objects.passive.platform

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.utils.TimeUtils
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.util.asset.texture.MyDrawable

open class HalfCollidedPlatform(game: ToddGame, drawable: MyDrawable, aabb: Rectangle) :
        SolidRectanglePlatform(game, drawable, aabb) {
    // TODO doesn't work for non-square polygons
    override fun isGroundFor(other: InGameObject) =
            MathUtils.isEqual(other.getAABB().y, getAABB().let { it.y + it.height }, 1f)

    override fun preSolve(other: InGameObject, contact: Contact, oldManifold: Manifold) {
        super.preSolve(other, contact, oldManifold)
        if (!isGroundFor(other)) {
            contact.isEnabled = false
        } else {
            processContact(other, contact, oldManifold)
        }
    }

    protected open fun processContact(other: InGameObject, contact: Contact, oldManifold: Manifold) {}
}
