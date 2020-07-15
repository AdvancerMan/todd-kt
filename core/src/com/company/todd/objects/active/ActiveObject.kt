package com.company.todd.objects.active

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.util.asset.texture.AnimationType
import com.company.todd.util.asset.texture.MySprite
import com.company.todd.util.box2d.bodyPattern.Sensor

import com.company.todd.util.box2d.bodyPattern.GroundSensorBodyPattern as GSBPattern

abstract class ActiveObject(game: ToddGame, sprite: MySprite, bodyPattern: GSBPattern,
                            private var speed: Float, private var jumpPower: Float) :
        InGameObject(game, sprite, RealBodyWrapper(bodyPattern)) {
    private val velocity = Vector2()
    private var changedAnimation = false
    private var groundsCount = 0

    init {
        bodyPattern.groundSensor = object : Sensor {
            override fun beginContact(other: InGameObject, contact: Contact) {
                super.beginContact(other, contact)
                groundsCount++
            }

            override fun endContact(other: InGameObject, contact: Contact) {
                super.endContact(other, contact)
                groundsCount--
            }
        }
    }

    abstract fun think(delta: Float)

    override fun act(delta: Float) {
        super.act(delta)

        velocity.setZero()
        think(delta)
        updateXVelocity()
        if (!velocity.epsilonEquals(velocity.x, 0f)) {
            updateYVelocity()
        }
    }

    protected fun updateAnimation() {
        if (sprite.playingType == AnimationType.JUMP && body.getVelocity().y <= 0f ||
                sprite.playingType != AnimationType.JUMP && !isOnGround()) {
            setPlayingType(AnimationType.FALL)
        }

        if (isOnGround() && (sprite.playingType == AnimationType.FALL || sprite.playingType == AnimationType.JUMP)) {
            setPlayingType(AnimationType.LANDING)
        }

        if (!changedAnimation && sprite.playingType != AnimationType.JUMP && sprite.playingType != AnimationType.FALL) {
            setPlayingType(AnimationType.STAY)
        }

        changedAnimation = false
    }

    override fun postAct(delta: Float) {
        super.postAct(delta)
        updateAnimation()
    }

    fun jump() {
        if (isOnGround()) {
            setPlayingType(AnimationType.JUMP, true)
            velocity.y = jumpPower
        }
    }

    fun run() {
        run(isDirectedToRight())
    }

    fun run(toRight: Boolean) {
        setPlayingType(AnimationType.RUN)
        velocity.x += if (toRight) speed else -speed
    }

    protected fun setPlayingType(type: AnimationType, forceReset: Boolean = false) {
        changedAnimation = true
        sprite.setPlayingType(type, forceReset)
    }

    protected fun updateXVelocity() {
        body.applyLinearImpulseToCenter(Vector2(velocity.x - body.getVelocity().x, 0f))
    }

    protected fun updateYVelocity() {
        body.setYVelocity(velocity.y)
    }

    fun isOnGround() = groundsCount > 0
}
