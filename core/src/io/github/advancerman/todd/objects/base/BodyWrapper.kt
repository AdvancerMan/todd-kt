package io.github.advancerman.todd.objects.base

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import io.github.advancerman.todd.box2d.bodyPattern.base.SensorName
import io.github.advancerman.todd.box2d.bodyPattern.sensor.Sensor
import io.github.advancerman.todd.json.ManuallyJsonSerializable
import io.github.advancerman.todd.screen.game.GameScreen


interface BodyWrapper : ManuallyJsonSerializable {
    fun init(gameScreen: GameScreen)

    fun putSensor(sensorName: SensorName, sensor: Sensor)

    fun applyLinearImpulseToCenter(impulse: Vector2)

    fun applyForceToCenter(force: Vector2)

    fun isFixedRotation(): Boolean

    fun isActive(): Boolean

    fun getCenter(): Vector2

    fun getVelocity(): Vector2

    fun getAngularVelocity(): Float

    fun getAngle(): Float

    fun setVelocity(v: Vector2)

    fun setVelocity(x: Float, y: Float) =
            setVelocity(Vector2(x, y))

    fun setYVelocity(yVel: Float) =
            setVelocity(getVelocity().x, yVel)

    fun setXVelocity(xVel: Float) =
            setVelocity(xVel, getVelocity().y)

    fun setAngularVelocity(velocity: Float)

    fun setCenter(x: Float, y: Float, resetLinearVelocity: Boolean = true)

    fun setCenter(v: Vector2, resetLinearVelocity: Boolean = true) =
        setCenter(v.x, v.y, resetLinearVelocity)

    fun setPosition(x: Float, y: Float, resetSpeed: Boolean = true) =
            getAABB().let {
                setCenter(x + it.width / 2, y + it.height / 2, resetSpeed)
            }

    fun setAngle(angle: Float, resetAngularVelocity: Boolean = true)

    fun setOwner(owner: InGameObject)

    fun setActive(value: Boolean)

    fun setBullet(value: Boolean)

    fun setGravityScale(value: Float)

    fun getUnrotatedAABB(): Rectangle

    fun getAABB(): Rectangle

    fun destroy(world: World)
}
