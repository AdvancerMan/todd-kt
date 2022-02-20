package io.github.advancerman.todd.objects.creature

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.gui.HealthBar
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.objects.base.InGameObject
import io.github.advancerman.todd.objects.base.RealBodyWrapper
import io.github.advancerman.todd.screen.game.GameScreen
import io.github.advancerman.todd.asset.texture.ToddDrawable
import io.github.advancerman.todd.asset.texture.animated.AnimationEvent
import io.github.advancerman.todd.asset.texture.animated.ToddAnimationEvent
import io.github.advancerman.todd.box2d.bodyPattern.base.BodyPattern
import io.github.advancerman.todd.box2d.bodyPattern.sensor.Sensor
import io.github.advancerman.todd.box2d.bodyPattern.base.SensorName
import io.github.advancerman.todd.box2d.bodyPattern.sensor.TopGroundListener
import io.github.advancerman.todd.json.*
import io.github.advancerman.todd.json.deserialization.construct
import io.github.advancerman.todd.launcher.game
import io.github.advancerman.todd.objects.base.DrawableActor
import io.github.advancerman.todd.objects.creature.behaviour.Behaviour
import io.github.advancerman.todd.thinker.Thinker
import io.github.advancerman.todd.thinker.operated.ScheduledThinker
import io.github.advancerman.todd.util.JUMP_COOLDOWN
import io.github.advancerman.todd.util.DAMAGE_TINT_TIME
import io.github.advancerman.todd.util.Y_VEL_JUMP_THRESHOLD

/**
 * Basic creature that combines different behaviours and thinkers
 *
 * @param drawable Base drawable for InGameObject.
 *                 Z-index is relative to object's actor,
 *                 offset is relative to unrotated, unflipped actor position
 * @param bodyPattern Body description for InGameObject
 * @param thinker AI for InGameObject
 * @param healthBar Health bar
 * @param behaviours Behaviours list
 * @param scale Actor's scale
 */
@SerializationType([InGameObject::class], "Creature")
class Creature(
    game: ToddGame,
    drawable: ToddDrawable,
    bodyPattern: BodyPattern,
    @JsonSaveSerializable var thinker: Thinker,
    @JsonUpdateSerializable val healthBar: HealthBar,
    val behaviours: List<Behaviour>,
    val enableCreatureCollisions: Boolean = false,
    scale: Float = 1f
) : InGameObject(game, drawable, RealBodyWrapper(bodyPattern), scale) {
    @JsonUpdateSerializable
    private var sinceJump = JUMP_COOLDOWN + 1
    @JsonUpdateSerializable
    private var sinceDamage = DAMAGE_TINT_TIME + 1
    var isOnGround = false
        // TODO think about sinceJump
        get() = field && body.getVelocity().y <= Y_VEL_JUMP_THRESHOLD //&& sinceJump >= JUMP_COOLDOWN
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
        behaviours.forEach { it.init(this, gameScreen) }
    }

    fun think(delta: Float) {
        thinker.think(delta, this, screen)
    }

    override fun act(delta: Float) {
        super.act(delta)
        sinceJump += delta
        sinceDamage += delta
        behaviours.forEach { it.update(delta, this, screen) }

        isOnGround = grounds.isNotEmpty()
        think(delta)
        behaviours.forEach { it.prePhysicsUpdate(delta, this, screen) }
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
        if (isOnGround) {
            reportAnimationEvent(ToddAnimationEvent.ON_GROUND)
        } else if (body.getVelocity().y <= 0) {
            reportAnimationEvent(ToddAnimationEvent.FALL)
        }
        behaviours.forEach { it.postUpdate(delta, this, screen) }
        drawable.getAdditionallyReportedEvents().forEach(::reportAnimationEventToChildren)
    }

    inline fun <reified T> getBehaviour(): T? {
        return behaviours.filterIsInstance<T>().firstOrNull()
    }

    override fun takeDamage(amount: Float) {
        super.takeDamage(amount)
        healthBar.value -= amount
        if (healthBar.value <= 0) {
            kill()
        }
        sinceDamage = 0f
    }

    override fun reportAnimationEvent(animationEvent: AnimationEvent) {
        super.reportAnimationEvent(animationEvent)
        reportAnimationEventToChildren(animationEvent)
    }

    private fun reportAnimationEventToChildren(animationEvent: AnimationEvent) {
        children.filterIsInstance<DrawableActor>()
            .filter { it.drawable !== drawable }
            .forEach { it.drawable!!.reportEvent(animationEvent, "owner") }
    }

    override fun preSolve(
        otherSensor: Sensor,
        other: InGameObject,
        myFixture: Fixture,
        otherFixture: Fixture,
        contact: Contact,
        oldManifold: Manifold
    ) {
        super.preSolve(otherSensor, other, myFixture, otherFixture, contact, oldManifold)
        if (other is Creature && !enableCreatureCollisions && !other.enableCreatureCollisions) {
            contact.isEnabled = false
        }
    }

    override fun dispose() {
        healthBar.dispose(game.textureManager)
        super.dispose()
    }

    companion object {
        @ManualJsonConstructor
        private fun getJsonDefaults(
            @Suppress("UNUSED_PARAMETER") json: JsonValue,
            parsed: MutableMap<String, Any?>
        ) {
            json["behaviours"]?.map { it.construct<Behaviour>(game) }?.also { parsed["behaviours"] = it }
            // TODO remove this default
            parsed.getOrPut("thinker") { ScheduledThinker() }
        }
    }
}
