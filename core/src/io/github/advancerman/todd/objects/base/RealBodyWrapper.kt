package io.github.advancerman.todd.objects.base

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.Shape.Type.*
import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.screen.game.GameScreen
import io.github.advancerman.todd.box2d.bodyPattern.base.BodyPattern
import io.github.advancerman.todd.box2d.bodyPattern.base.SensorName
import io.github.advancerman.todd.box2d.bodyPattern.sensor.Sensor
import io.github.advancerman.todd.json.ManuallyJsonSerializable
import io.github.advancerman.todd.json.deserialization.float
import io.github.advancerman.todd.json.deserialization.get
import io.github.advancerman.todd.json.deserialization.vector
import io.github.advancerman.todd.json.serialization.toJsonValue
import io.github.advancerman.todd.util.rotateAround
import io.github.advancerman.todd.util.translate

const val pixInMeter = 30f

fun Float.toPix() = this * pixInMeter
fun Float.toMeters() = this / pixInMeter

fun Vector2.toPix() = this.scl(pixInMeter)!!
fun Vector2.toMeters() = this.scl(1 / pixInMeter)!!

fun Rectangle.toPix() = this.set(x.toPix(), y.toPix(), width.toPix(), height.toPix())!!
fun Rectangle.toMeters() = this.set(x.toMeters(), y.toMeters(), width.toMeters(), height.toMeters())!!

class RealBodyWrapper(private val bodyPattern: BodyPattern) : BodyWrapper, ManuallyJsonSerializable by bodyPattern {
    private lateinit var body: Body

    override fun init(gameScreen: GameScreen) {
        body = gameScreen.createBody(bodyPattern)
    }

    override fun putSensor(sensorName: SensorName, sensor: Sensor) {
        bodyPattern.sensors[sensorName] = sensor
    }

    override fun applyLinearImpulseToCenter(impulse: Vector2) {
        body.applyLinearImpulse(impulse.cpy().toMeters(), body.worldCenter, true)
    }

    override fun applyForceToCenter(force: Vector2) {
        body.applyForceToCenter(force.cpy().toMeters(), true)
    }

    override fun isFixedRotation() = body.isFixedRotation

    override fun isActive() = body.isActive

    override fun getAngle() = body.angle

    override fun getCenter() = body.position.cpy().toPix()

    override fun getVelocity() = body.linearVelocity.cpy().toPix()

    override fun getAngularVelocity() = body.angularVelocity

    override fun setVelocity(v: Vector2) {
        body.linearVelocity = v.toMeters()
    }

    override fun setAngularVelocity(velocity: Float) {
        body.angularVelocity = velocity
    }

    override fun setCenter(x: Float, y: Float, resetLinearVelocity: Boolean) {
        body.setTransform(x.toMeters(), y.toMeters(), body.angle)
        if (resetLinearVelocity) {
            body.setLinearVelocity(0f, 0f)
        }
    }

    override fun setAngle(angle: Float, resetAngularVelocity: Boolean) {
        if (resetAngularVelocity) {
            body.angularVelocity = 0f
        }
        body.setTransform(body.position, angle)
    }

    override fun setOwner(owner: InGameObject) {
        body.userData = owner
    }

    override fun setActive(value: Boolean) {
        body.isActive = value
    }

    override fun setBullet(value: Boolean) {
        body.isBullet = value
    }

    override fun setGravityScale(value: Float) {
        body.gravityScale = value
    }

    override fun getUnrotatedAABB() =
            // TODO (0, 0) isn't always in AABB
            Rectangle().apply {
                val tmp = Vector2()
                body.fixtureList.forEach {
                    if (!it.isSensor) {
                        merge(tmp, it.shape)
                    }
                }
                translate(body.position).toPix()
            }

    override fun getAABB() =
            getUnrotatedAABB().rotateAround(getCenter(), body.angle)

    override fun destroy(world: World) {
        world.destroyBody(body)
    }

    override fun serializeUpdates(json: JsonValue) {
        json.addChild("b2d_position", getCenter().toJsonValue())
        json.addChild("b2d_linearVelocity", getVelocity().toJsonValue())
        json.addChild("b2d_gravityScale", body.gravityScale.toJsonValue())
        json.addChild("b2d_angularVelocity", getAngularVelocity().toJsonValue())
        json.addChild("b2d_angle", getAngle().toJsonValue())
    }

    override fun deserializeUpdates(json: JsonValue) {
        setCenter(json["b2d_position", vector], false)
        setVelocity(json["b2d_linearVelocity", vector])
        body.gravityScale = json["b2d_gravityScale", float]
        setAngularVelocity(json["b2d_angularVelocity", float])
        setAngle(json["b2d_angle", float], false)
    }
}

private fun Rectangle.merge(tmp: Vector2, shape: Shape) {
    when (shape.type) {
        Polygon -> merge(tmp, shape as PolygonShape)
        Circle -> merge(shape as CircleShape)
        Edge -> merge(tmp, shape as EdgeShape)
        Chain -> merge(tmp, shape as ChainShape)
        else -> Gdx.app.error("BodyWrapper", "Unexpected shape ${shape.type} in getAABB")
    }
}

private fun Rectangle.merge(tmp: Vector2, polygonShape: PolygonShape) {
    for (i in 0 until polygonShape.vertexCount) {
        polygonShape.getVertex(i, tmp)
        merge(tmp)
    }
}

private fun Rectangle.merge(circleShape: CircleShape) {
    Rectangle().apply {
        setSize(2 * circleShape.radius)
        setCenter(circleShape.position)
        this@merge.merge(this)
    }
}

private fun Rectangle.merge(tmp: Vector2, edgeShape: EdgeShape) {
    edgeShape.getVertex1(tmp)
    merge(tmp)
    edgeShape.getVertex2(tmp)
    merge(tmp)
}

private fun Rectangle.merge(tmp: Vector2, chainShape: ChainShape) {
    for (i in 0 until chainShape.vertexCount) {
        chainShape.getVertex(i, tmp)
        merge(tmp)
    }
}
