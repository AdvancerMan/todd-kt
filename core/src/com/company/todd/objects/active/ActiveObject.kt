package com.company.todd.objects.active

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.util.asset.texture.sprite.AnimationType
import com.company.todd.util.asset.texture.drawable.MyDrawable
import com.company.todd.util.box2d.bodyPattern.Sensor

import com.company.todd.util.box2d.bodyPattern.GroundSensorBodyPattern as GSBPattern

abstract class ActiveObject(game: ToddGame, drawable: MyDrawable, bodyPattern: GSBPattern,
                            private var speed: Float, private var jumpPower: Float) :
        InGameObject(game, drawable, RealBodyWrapper(bodyPattern)) {
    private val preVelocity = Vector2()
    private var changedAnimation = false
    private var isOnGround = false
    private val grounds = mutableMapOf<InGameObject, Int>()

    init {
        bodyPattern.groundSensor = object : Sensor {
            override fun beginContact(other: InGameObject, contact: Contact) {
                super.beginContact(other, contact)
                grounds[other] = grounds.getOrPut(other) { 0 } + 1
            }

            override fun endContact(other: InGameObject, contact: Contact) {
                super.endContact(other, contact)
                if (grounds[other]!!.dec() == 0) {
                    grounds.remove(other)
                }
            }
        }
    }

    abstract fun think(delta: Float)

    override fun act(delta: Float) {
        super.act(delta)

        preVelocity.setZero()
        isOnGround = grounds.any { it.key.isGroundFor(this) }
        think(delta)
        updateXVelocity()
        if (!preVelocity.epsilonEquals(preVelocity.x, 0f)) {
            updateYVelocity()
        }
    }

    protected fun updateAnimation() {
        if (getPlayingType() == AnimationType.JUMP && getVelocity().y <= 0f ||
                getPlayingType() != AnimationType.JUMP && !isOnGround) {
            setPlayingType(AnimationType.FALL)
        }

        if (isOnGround && (getPlayingType() == AnimationType.FALL || getPlayingType() == AnimationType.JUMP)) {
            setPlayingType(AnimationType.LANDING)
        }

        if (!changedAnimation && getPlayingType() != AnimationType.JUMP && getPlayingType() != AnimationType.FALL) {
            setPlayingType(AnimationType.STAY)
        }

        changedAnimation = false
    }

    override fun postAct(delta: Float) {
        super.postAct(delta)
        updateAnimation()
    }

    fun jump() {
        if (isOnGround) {
            setPlayingType(AnimationType.JUMP, true)
            preVelocity.y = jumpPower
        }
    }

    fun run() {
        run(isDirectedToRight())
    }

    fun run(toRight: Boolean) {
        setPlayingType(AnimationType.RUN)
        preVelocity.x += if (toRight) speed else -speed
    }

    override fun setPlayingType(type: AnimationType, forceReset: Boolean) {
        changedAnimation = true
        super.setPlayingType(type, forceReset)
    }

    protected fun updateXVelocity() {
        applyLinearImpulseToCenter(Vector2(preVelocity.x - getVelocity().x, 0f))
    }

    protected fun updateYVelocity() {
        setYVelocity(preVelocity.y)
    }
}
