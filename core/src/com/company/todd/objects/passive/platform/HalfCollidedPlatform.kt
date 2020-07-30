package com.company.todd.objects.passive.platform

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Manifold
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.util.asset.texture.MyDrawable
import com.company.todd.util.box2d.bodyPattern.sensor.Sensor

open class HalfCollidedPlatform(game: ToddGame, drawable: MyDrawable, aabb: Rectangle) :
        SolidRectanglePlatform(game, drawable, aabb) {
    override fun preSolve(otherSensor: Sensor, other: InGameObject, contact: Contact, oldManifold: Manifold) {
        super.preSolve(otherSensor, other, contact, oldManifold)
        TODO("Contact with HalfCol is not implemented. Please remove it from the level.")
//        if (!isGroundFor(other)) {
//            contact.isEnabled = false
//        } else {
//            processContact(other, contact, oldManifold)
//        }
    }

    protected open fun processContact(other: InGameObject, contact: Contact, oldManifold: Manifold) {}
}
