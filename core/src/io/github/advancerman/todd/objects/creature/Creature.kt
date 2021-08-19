package io.github.advancerman.todd.objects.creature

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.gui.HealthBar
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.objects.base.InGameObject
import io.github.advancerman.todd.objects.base.RealBodyWrapper
import io.github.advancerman.todd.screen.game.GameScreen
import io.github.advancerman.todd.asset.texture.animated.AnimationType
import io.github.advancerman.todd.asset.texture.ToddDrawable
import io.github.advancerman.todd.asset.texture.animated.stayAnimation
import io.github.advancerman.todd.box2d.bodyPattern.base.BodyPattern
import io.github.advancerman.todd.box2d.bodyPattern.sensor.Sensor
import io.github.advancerman.todd.box2d.bodyPattern.base.SensorName
import io.github.advancerman.todd.box2d.bodyPattern.sensor.TopGroundListener
import io.github.advancerman.todd.json.*
import io.github.advancerman.todd.objects.weapon.Weapon
import io.github.advancerman.todd.thinker.Thinker
import io.github.advancerman.todd.thinker.operated.ScheduledThinker
import io.github.advancerman.todd.thinker.operated.ThinkerAction
import io.github.advancerman.todd.util.HEALTH_BAR_OFFSET
import io.github.advancerman.todd.util.JUMP_COOLDOWN
import io.github.advancerman.todd.util.DAMAGE_TINT_TIME
import io.github.advancerman.todd.util.Y_VEL_JUMP_THRESHOLD

/**
 * Basic creature that can run, jump and attack
 *
 * @param drawable Base drawable for InGameObject.
 *                 Z-index is relative to object's actor,
 *                 offset is relative to unrotated, unflipped actor position
 * @param bodyPattern Body description for InGameObject
 * @param weapon Weapon
 * @param thinker AI for InGameObject
 * @param healthBar Health bar
 * @param speed Speed
 * @param jumpPower Jump power
 * @param scale Actor's scale
 */
@SerializationType(InGameObject::class, "Creature")
open class Creature(
    game: ToddGame, drawable: ToddDrawable, bodyPattern: BodyPattern,
    @JsonUpdateSerializable protected var weapon: Weapon?,
    @JsonSaveSerializable var thinker: Thinker,
    @JsonUpdateSerializable val healthBar: HealthBar,
    @JsonUpdateSerializable private var speed: Float,
    @JsonUpdateSerializable private var jumpPower: Float,
    scale: Float = 1f
) : InGameObject(game, drawable, RealBodyWrapper(bodyPattern), scale) {
    private val preVelocity = Vector2()
    private var preferredAnimationType: AnimationType = "STAY"
    protected var animationTypeNow = stayAnimation()
    @JsonUpdateSerializable
    private var sinceJump = JUMP_COOLDOWN + 1
    @JsonUpdateSerializable
    private var sinceDamage = DAMAGE_TINT_TIME + 1
    var isOnGround = false
        get() = field && body.getVelocity().y <= Y_VEL_JUMP_THRESHOLD && sinceJump >= JUMP_COOLDOWN
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
            it.setOwnerTopCenter(width / 2, height)
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

        preferredAnimationType = "STAY"
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
        drawable.setPlayingType(animationTypeNow.type)
        weapon?.postUpdate(delta)
    }

    fun jump() {
        if (isOnGround) {
            sinceJump = 0f
            preferredAnimationType = "JUMP"
            preVelocity.y = jumpPower
        }
        screen.listenAction(ThinkerAction.JUMP, this)
    }

    fun run() {
        run(isDirectedToRight)
    }

    fun run(toRight: Boolean) {
        if (drawable.getPlayingType() != "JUMP") {
            preferredAnimationType = "RUN"
        }
        preVelocity.x += if (toRight) speed else -speed

        if (toRight) {
            screen.listenAction(ThinkerAction.RUN_RIGHT, this)
        } else {
            screen.listenAction(ThinkerAction.RUN_LEFT, this)
        }
    }

    protected fun updateXVelocity() {
        body.applyLinearImpulseToCenter(Vector2(preVelocity.x - body.getVelocity().x, 0f))
    }

    protected fun updateYVelocity() {
        body.setYVelocity(preVelocity.y)
    }

    fun canAttack() = weapon?.canAttack() ?: false

    fun attack() {
        weapon?.attack()
        screen.listenAction(ThinkerAction.ATTACK, this)
    }

    override fun takeDamage(amount: Float) {
        super.takeDamage(amount)
        healthBar.value -= amount
        if (healthBar.value <= 0) {
            kill()
        }
        sinceDamage = 0f
    }

    override fun dispose() {
        healthBar.dispose(game.textureManager)
        super.dispose()
    }

    companion object {
        @ManualJsonConstructor
        private fun getJsonDefaults(
            @Suppress("UNUSED_PARAMETER") json: JsonValue,
            parsed: MutableMap<String, Pair<Any?, Boolean>>
        ) {
            // TODO remove this default
            JsonDefaults.setDefault("thinker", ScheduledThinker(), parsed)
        }
    }
}
