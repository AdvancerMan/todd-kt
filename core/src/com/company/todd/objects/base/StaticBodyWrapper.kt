package com.company.todd.objects.base

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.company.todd.screen.GameScreen

class StaticBodyWrapper(private val aabb: Rectangle): BodyWrapper {
    constructor(x: Float, y: Float, w: Float, h: Float) : this(Rectangle(x, y, w, h))

    override fun getCenter() =
            Vector2(aabb.x + aabb.width / 2, aabb.y + aabb.height / 2)

    override fun setCenter(x: Float, y: Float, resetLinearVelocity: Boolean) {
        aabb.setCenter(x, y)
    }

    override fun getAABB() = Rectangle(aabb)

    override fun init(gameScreen: GameScreen) {}
    override fun setAngle(angle: Float, resetAngularVelocity: Boolean) {}
    override fun applyLinearImpulseToCenter(impulse: Vector2) {}
    override fun getVelocity() = Vector2()
    override fun setVelocity(v: Vector2) {}
    override fun destroy(world: World) {}
}
