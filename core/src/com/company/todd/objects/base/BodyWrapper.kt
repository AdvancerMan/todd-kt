package com.company.todd.objects.base

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.company.todd.screen.GameScreen


interface BodyWrapper {
    fun init(gameScreen: GameScreen)

    fun applyLinearImpulseToCenter(impulse: Vector2)

    fun isFixedRotation(): Boolean

    fun getCenter(): Vector2

    fun getVelocity(): Vector2

    fun getAngle(): Float

    fun setVelocity(v: Vector2)

    fun setVelocity(x: Float, y: Float) =
            setVelocity(Vector2(x, y))

    fun setYVelocity(yVel: Float) =
            setVelocity(getVelocity().x, yVel)

    fun setXVelocity(xVel: Float) =
            setVelocity(xVel, getVelocity().y)

    fun setCenter(x: Float, y: Float, resetLinearVelocity: Boolean = true)

    fun setPosition(x: Float, y: Float, resetSpeed: Boolean = true) =
            getAABB().let {
                setCenter(x + it.width / 2, y + it.height / 2, resetSpeed)
            }

    fun setAngle(angle: Float, resetAngularVelocity: Boolean = true)

    fun setOwner(owner: InGameObject)

    fun getAABB(): Rectangle

    fun destroy(world: World)
}
