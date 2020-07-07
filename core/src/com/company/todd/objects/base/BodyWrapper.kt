package com.company.todd.objects.base

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.company.todd.screen.GameScreen


interface BodyWrapper {
    fun init(gameScreen: GameScreen)

    fun applyLinearImpulseToCenter(impulse: Vector2)

    fun getPosition(): Vector2

    fun setVelocity(v: Vector2)

    fun setYVelocity(yVel: Float)

    fun setXVelocity(xVel: Float)

    fun setCenterPosition(x: Float, y: Float, resetLinearVelocity: Boolean = true)

    fun setPosition(x: Float, y: Float, resetSpeed: Boolean = true)

    fun setAngle(angle: Float, resetAngularVelocity: Boolean = true)

    fun getAABB(): Rectangle

    fun destroy(world: World)
}
