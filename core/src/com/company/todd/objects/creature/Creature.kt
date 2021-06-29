package com.company.todd.objects.creature

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.utils.Align
import com.company.todd.gui.HealthBar
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.screen.GameScreen
import com.company.todd.asset.texture.animated.AnimationType
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.asset.texture.animated.stayAnimation
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.box2d.bodyPattern.sensor.Sensor
import com.company.todd.box2d.bodyPattern.base.SensorName
import com.company.todd.box2d.bodyPattern.sensor.TopGroundListener
import com.company.todd.json.JsonSaveSerializable
import com.company.todd.json.JsonUpdateSerializable
import com.company.todd.json.SerializationType
import com.company.todd.objects.weapon.Weapon
import com.company.todd.thinker.Thinker
import com.company.todd.thinker.operated.ThinkerAction
import com.company.todd.util.HEALTH_BAR_OFFSET
import com.company.todd.util.JUMP_COOLDOWN
import com.company.todd.util.DAMAGE_TINT_TIME
import com.company.todd.util.Y_VEL_JUMP_THRESHOLD

@SerializationType("creature")
open class Creature(
    game: ToddGame, drawable: MyDrawable, drawableSize: Vector2,
    bodyLowerLeftCornerOffset: Vector2, bodyPattern: BodyPattern,
    @JsonUpdateSerializable protected var weapon: Weapon?,
    @JsonSaveSerializable val thinker: Thinker,
    @JsonUpdateSerializable private val healthBar: HealthBar,
    @JsonUpdateSerializable private var speed: Float,
    @JsonUpdateSerializable private var jumpPower: Float
) : InGameObject(game, drawable, drawableSize, bodyLowerLeftCornerOffset, RealBodyWrapper(bodyPattern)) {
    private val preVelocity = Vector2()
    private var preferredAnimationType = AnimationType.STAY
    protected var animationTypeNow = stayAnimation()
    @JsonUpdateSerializable
    private var sinceJump = JUMP_COOLDOWN + 1
    @JsonUpdateSerializable
    private var sinceDamage = DAMAGE_TINT_TIME + 1
    var isOnGround = false
        get() = field && getVelocity().y <= Y_VEL_JUMP_THRESHOLD && sinceJump >= JUMP_COOLDOWN
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
                        otherSensor.beginOnGround(this@Creature)
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
                        otherSensor.endOnGround(this@Creature)
                        grounds.remove(other)
                    } else {
                        grounds[other] = cnt
                    }
                }
            }
        }
    }

    override fun doInit(gameScreen: GameScreen) {
        super.doInit(gameScreen)
        healthBar.let {
            it.setPosition(width / 2, height + HEALTH_BAR_OFFSET, Align.bottom or Align.center)
            addActor(it)
        }
        weapon?.let {
            addActor(it)
            it.init(this, gameScreen)
        }
    }

    fun think(delta: Float) {
        thinker.think(delta, this, screen)
    }

    override fun act(delta: Float) {
        super.act(delta)
        sinceJump += delta
        sinceDamage += delta

        preferredAnimationType = AnimationType.STAY
        preVelocity.setZero()
        isOnGround = grounds.isNotEmpty()
        think(delta)
        updateXVelocity()
        if (!preVelocity.epsilonEquals(preVelocity.x, 0f)) {
            updateYVelocity()
        }
    }

    override fun updateColor() {
        super.updateColor()
        if (sinceDamage < DAMAGE_TINT_TIME) {
            val gbColor = Interpolation.smooth.apply(sinceDamage / DAMAGE_TINT_TIME)
            color.g *= gbColor
            color.b *= gbColor
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
            screen.listenAction(ThinkerAction.JUMP, this)
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

        if (toRight) {
            screen.listenAction(ThinkerAction.RUN_RIGHT, this)
        } else {
            screen.listenAction(ThinkerAction.RUN_LEFT, this)
        }
    }

    protected fun updateXVelocity() {
        applyLinearImpulseToCenter(Vector2(preVelocity.x - getVelocity().x, 0f))
    }

    protected fun updateYVelocity() {
        setYVelocity(preVelocity.y)
    }

    fun canAttack() = weapon?.canAttack() ?: false

    fun attack() {
        weapon?.attack()
        screen.listenAction(ThinkerAction.ATTACK, this)
    }

    override fun takeDamage(amount: Float) {
        super.takeDamage(amount)
        healthBar.value -= amount
        sinceDamage = 0f
    }

    override fun dispose() {
        healthBar.dispose(game.textureManager)
        super.dispose()
    }
}
