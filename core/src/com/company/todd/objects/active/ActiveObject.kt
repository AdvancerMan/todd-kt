package com.company.todd.objects.active

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.util.asset.texture.animated.AnimationType
import com.company.todd.util.asset.texture.MyDrawable
import com.company.todd.util.asset.texture.animated.stayAnimation
import com.company.todd.util.box2d.bodyPattern.base.BodyPattern
import com.company.todd.util.box2d.bodyPattern.sensor.Sensor
import com.company.todd.util.box2d.bodyPattern.base.SensorName
import com.company.todd.util.box2d.bodyPattern.sensor.TopGroundListener
import com.company.todd.util.box2d.bodyPattern.sensor.TopGroundSensor

const val JUMP_COOLDOWN = 1 / 30f
const val yVelJumpThreshold = 1f

abstract class ActiveObject(game: ToddGame, drawable: MyDrawable, bodyPattern: BodyPattern,
                            private var speed: Float, private var jumpPower: Float) :
        InGameObject(game, drawable, RealBodyWrapper(bodyPattern)) {
    private val preVelocity = Vector2()
    private var preferredAnimationType = AnimationType.STAY
    protected var animationTypeNow = stayAnimation()
    private var sinceJump = JUMP_COOLDOWN + 1
    var isOnGround = false
        get() = field && getVelocity().y <= yVelJumpThreshold && sinceJump >= JUMP_COOLDOWN
        private set

    private val grounds = mutableMapOf<InGameObject, Int>()

    init {
        bodyPattern.sensors[SensorName.BOTTOM_GROUND_SENSOR] = object : Sensor {
            override fun beginContact(otherSensor: Sensor, other: InGameObject, myFixture: Fixture,
                                      otherFixture: Fixture, contact: Contact) {
                super.beginContact(otherSensor, other, myFixture, otherFixture, contact)
                if (!(otherSensor === other) && otherSensor is TopGroundListener) {
                    val cnt = grounds.getOrElse(other) { 0 }
                    if (cnt == 0) {
                        otherSensor.beginOnGround(this@ActiveObject)
                    }
                    grounds[other] = cnt + 1
                }
            }

            override fun endContact(otherSensor: Sensor, other: InGameObject, myFixture: Fixture,
                                    otherFixture: Fixture, contact: Contact) {
                super.endContact(otherSensor, other, myFixture, otherFixture, contact)
                if (!(otherSensor === other) && otherSensor is TopGroundListener) {
                    val cnt = grounds[other]!! - 1
                    if (cnt == 0) {
                        otherSensor.endOnGround(this@ActiveObject)
                        grounds.remove(other)
                    } else {
                        grounds[other] = cnt
                    }
                }
            }
        }

        // it is guaranteed that link to this is not used by sensor while this creates
        @Suppress("LeakingThis")
        bodyPattern.sensors[SensorName.TOP_GROUND_SENSOR] = TopGroundSensor(this)
    }

    abstract fun think(delta: Float)

    override fun act(delta: Float) {
        super.act(delta)
        sinceJump += delta

        preferredAnimationType = AnimationType.STAY
        preVelocity.setZero()
        isOnGround = grounds.isNotEmpty()
        think(delta)
        updateXVelocity()
        if (!preVelocity.epsilonEquals(preVelocity.x, 0f)) {
            updateYVelocity()
        }
    }

    override fun postAct(delta: Float) {
        super.postAct(delta)
        animationTypeNow = animationTypeNow.next(this, preferredAnimationType)
        setPlayingType(animationTypeNow.type)
    }

    fun jump() {
        if (isOnGround) {
            sinceJump = 0f
            preferredAnimationType = AnimationType.JUMP
            preVelocity.y = jumpPower
        }
    }

    fun run() {
        run(isDirectedToRight)
    }

    fun run(toRight: Boolean) {
        if (getPlayingType() != AnimationType.JUMP) {
            preferredAnimationType = AnimationType.RUN
        }
        preVelocity.x += if (toRight) speed else -speed
    }

    protected fun updateXVelocity() {
        applyLinearImpulseToCenter(Vector2(preVelocity.x - getVelocity().x, 0f))
    }

    protected fun updateYVelocity() {
        setYVelocity(preVelocity.y)
    }
}
