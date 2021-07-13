package com.company.todd.objects.weapon.bullet

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.box2d.bodyPattern.createCircleBP
import com.company.todd.box2d.bodyPattern.sensor.Sensor
import com.company.todd.json.JsonConstructorDefaults
import com.company.todd.json.JsonFullSerializable
import com.company.todd.json.SerializationType
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.screen.game.GameScreen

@SerializationType("bullet", "simpleBullet")
open class Bullet(
    game: ToddGame, drawable: MyDrawable,
    drawableSize: Vector2?, bodyLowerLeftCornerOffset: Vector2,
    bodyPattern: BodyPattern,
    @JsonFullSerializable protected val power: Float,
    // TODO change to velocity
    @JsonFullSerializable protected val tmp_velocity: Vector2,
    @JsonFullSerializable protected val ownerFriendlyPeriod: Float,
    // TODO serialize igo???
    @JsonFullSerializable protected val owner: InGameObject
) : InGameObject(game, drawable, drawableSize, bodyLowerLeftCornerOffset, RealBodyWrapper(bodyPattern)) {
    private var sinceCreation = 0f

    override fun doInit(gameScreen: GameScreen) {
        super.doInit(gameScreen)
        body.setBullet(true)
    }

    override fun act(delta: Float) {
        super.act(delta)
        sinceCreation += delta
        body.setVelocity(tmp_velocity.cpy())
    }

    override fun beginContact(
        otherSensor: Sensor,
        other: InGameObject,
        myFixture: Fixture,
        otherFixture: Fixture,
        contact: Contact
    ) {
        super.beginContact(otherSensor, other, myFixture, otherFixture, contact)
        if (
            (other != owner || other == owner && sinceCreation > ownerFriendlyPeriod)
            && !myFixture.isSensor && !otherFixture.isSensor
        ) {
            other.takeDamage(power)
            takeDamage(Float.MAX_VALUE)
        }
    }

    override fun takeDamage(amount: Float) {
        super.takeDamage(amount)
        kill()
    }

    interface Builder {
        fun build(game: ToddGame, power: Float, position: Vector2, direction: Vector2, owner: InGameObject): Bullet
    }

    @SerializationType("bulletBuilder", "simpleBuilder")
    data class SimpleBuilder(
        @JsonFullSerializable private val drawableName: String,
        @JsonFullSerializable private val drawableSize: Vector2?,
        @JsonFullSerializable private val bodyLowerLeftCornerOffset: Vector2,
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
                drawableSize, bodyLowerLeftCornerOffset,
                createCircleBP(BodyDef.BodyType.DynamicBody, position.add(radius, radius), radius),
                power,
                direction.apply {
                    x *= speed
                    y *= speed
                }, ownerFriendlyPeriod, owner
            )
        }

        companion object {
            @JsonConstructorDefaults
            private fun getJsonConstructorDefaults(parsed: MutableMap<String, Pair<Any?, Boolean>>) {
                InGameObject.getJsonConstructorDefaults(parsed)
            }
        }
    }
}