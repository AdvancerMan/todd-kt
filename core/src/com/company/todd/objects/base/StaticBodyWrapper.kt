package com.company.todd.objects.base

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.company.todd.screen.GameScreen
import com.company.todd.util.rotate

class StaticBodyWrapper(private val unrotatedAABB: Rectangle, private var angle: Float): BodyWrapper {
    constructor(x: Float, y: Float, w: Float, h: Float, angle: Float) : this(Rectangle(x, y, w, h), angle)

    override fun isFixedRotation() = true
    override fun isActive() = false

    override fun getAngle() = angle

    override fun setAngle(angle: Float, resetAngularVelocity: Boolean) {
        this.angle = angle
    }

    override fun getCenter() =
            Vector2(unrotatedAABB.x + unrotatedAABB.width / 2, unrotatedAABB.y + unrotatedAABB.height / 2)

    override fun setCenter(x: Float, y: Float, resetLinearVelocity: Boolean) {
        unrotatedAABB.setCenter(x, y)
    }

    override fun getUnrotatedAABB() = Rectangle(unrotatedAABB)
    override fun getAABB() = getUnrotatedAABB().rotate(angle)

    override fun init(gameScreen: GameScreen) {}
    override fun applyLinearImpulseToCenter(impulse: Vector2) {}
    override fun applyForceToCenter(force: Vector2) {}
    override fun getVelocity() = Vector2()
    override fun setVelocity(v: Vector2) {}
    override fun setOwner(owner: InGameObject) {}
    override fun setActive(value: Boolean) {}

    override fun destroy(world: World) {}
}
