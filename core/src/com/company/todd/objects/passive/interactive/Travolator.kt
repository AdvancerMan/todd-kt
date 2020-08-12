package com.company.todd.objects.passive.interactive

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Manifold
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.passive.platform.HalfCollidedPlatform
import com.company.todd.util.asset.texture.MyDrawable
import kotlin.math.abs

private val objectToTravolatorNegativeImpulse = mutableMapOf<InGameObject, Float>()
private val objectToTravolatorPositiveImpulse = mutableMapOf<InGameObject, Float>()

class Travolator(game: ToddGame, drawable: MyDrawable, aabb: Rectangle,
                 drawableSize: Vector2, bodyLowerLeftCornerOffset: Vector2,
                 private val pushPower: Float) :
        HalfCollidedPlatform(game, drawable, aabb, drawableSize, bodyLowerLeftCornerOffset) {
    override fun processContact(other: InGameObject, contact: Contact, oldManifold: Manifold) {
        super.processContact(other, contact, oldManifold)
        val impulseMap = if (pushPower > 0) objectToTravolatorPositiveImpulse else objectToTravolatorNegativeImpulse
        val impulse = impulseMap[other] ?: 0f
        if (abs(impulse) < abs(pushPower)) {
            other.applyLinearImpulseToCenter(Vector2(pushPower - impulse, 0f))
            impulseMap[other] = pushPower
        }
    }

    override fun postAct(delta: Float) {
        super.postAct(delta)
        objectToTravolatorNegativeImpulse.clear()
        objectToTravolatorPositiveImpulse.clear()
    }
}
