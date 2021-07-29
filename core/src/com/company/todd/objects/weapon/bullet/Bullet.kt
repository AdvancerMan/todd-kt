package com.company.todd.objects.weapon.bullet

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.box2d.bodyPattern.BodyPatterns
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.box2d.bodyPattern.sensor.Sensor
import com.company.todd.json.ManualJsonConstructor
import com.company.todd.json.JsonFullSerializable
import com.company.todd.json.SerializationType
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.screen.game.GameScreen

@SerializationType(InGameObject::class, "simpleBullet")
open class Bullet(
    game: ToddGame, drawable: MyDrawable, bodyPattern: BodyPattern,
    @JsonFullSerializable protected val power: Float,
    @JsonFullSerializable protected val velocity: Vector2,
    @JsonFullSerializable protected val ownerFriendlyPeriod: Float,
    // TODO serialize igo???
    @JsonFullSerializable protected val owner: InGameObject?,
    scale: Float
) : InGameObject(game, drawable, RealBodyWrapper(bodyPattern), scale) {
    private var sinceCreation = 0f

    override fun doInit(gameScreen: GameScreen) {
        super.doInit(gameScreen)
        body.setBullet(true)
        body.setAngle(velocity.angleRad())
    }

    override fun act(delta: Float) {
        super.act(delta)
        sinceCreation += delta
        body.setVelocity(velocity.cpy())
    }

    override fun beginContact(
        otherSensor: Sensor, other: InGameObject,
        myFixture: Fixture, otherFixture: Fixture,
        contact: Contact
    ) {
        if (
            (other != owner || other == owner && sinceCreation > ownerFriendlyPeriod)
            && !myFixture.isSensor && !otherFixture.isSensor && alive
        ) {
            other.takeDamage(power)
            takeDamage(Float.MAX_VALUE)
        }
        contact.isEnabled = false
        contact.tangentSpeed = 0f
    }

    override fun preSolve(
        otherSensor: Sensor, other: InGameObject,
        myFixture: Fixture, otherFixture: Fixture,
        contact: Contact, oldManifold: Manifold
    ) {
        beginContact(otherSensor, other, myFixture, otherFixture, contact)
    }

    override fun takeDamage(amount: Float) {
        super.takeDamage(amount)
        kill()
    }

    interface Builder {
        fun build(game: ToddGame, power: Float, position: Vector2, direction: Vector2, owner: InGameObject): Bullet
    }

    // TODO bullet serialization from json (not from builder)
    @SerializationType(Builder::class, "simpleBuilder")
    data class SimpleBuilder(
        @JsonFullSerializable private val drawableName: String,
        @JsonFullSerializable private val radius: Float,
        @JsonFullSerializable private val speed: Float,
        @JsonFullSerializable private val ownerFriendlyPeriod: Float
    ) : Builder {
        override fun build(
            game: ToddGame, power: Float, position: Vector2,
            direction: Vector2, owner: InGameObject
        ): Bullet {
            return Bullet(
                game, game.textureManager.loadDrawable(drawableName),
                BodyPatterns.createCircleBP(BodyDef.BodyType.DynamicBody, position.add(radius, radius), radius, 1f),
                power,
                direction.apply {
                    x *= speed
                    y *= speed
                }, ownerFriendlyPeriod, owner, 1f
            )
        }
    }
}
