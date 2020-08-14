package com.company.todd.objects.base

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.Shape.Type.*
import com.company.todd.screen.GameScreen
import com.company.todd.box2d.bodyPattern.base.BodyPattern

const val pixInMeter = 30f

fun Float.toPix() = this * pixInMeter
fun Float.toMeters() = this / pixInMeter

fun Vector2.toPix() = this.scl(pixInMeter)!!
fun Vector2.toMeters() = this.scl(1 / pixInMeter)!!

fun Rectangle.toPix() = this.set(x.toPix(), y.toPix(), width.toPix(), height.toPix())!!
fun Rectangle.toMeters() = this.set(x.toMeters(), y.toMeters(), width.toMeters(), height.toMeters())!!

class RealBodyWrapper(private val bodyPattern: BodyPattern) : BodyWrapper {
    private lateinit var body: Body

    override fun init(gameScreen: GameScreen) {
        body = gameScreen.createBody(bodyPattern)
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

    override fun setVelocity(v: Vector2) {
        body.linearVelocity = v.toMeters()
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
}

fun Rectangle.translate(trX: Float, trY: Float) =
        setPosition(x + trX, y + trY)

fun Rectangle.translate(v: Vector2) =
        translate(v.x, v.y)

fun Rectangle.rotate(angle: Float) =
        rotateRad(angle * MathUtils.degreesToRadians)

fun Rectangle.rotateRad(angleRad: Float) =
        listOf(
                Vector2(x, y),
                Vector2(x + width, y),
                Vector2(x, y + height),
                Vector2(x + width, y + height)
        )
                .map { it.rotateRad(angleRad) }
                .also { this.set(it[0].x, it[0].y, 0f, 0f) }
                .fold(this) { r, v -> r.merge(v) }!!

fun Rectangle.rotateAround(originX: Float, originY: Float, angle: Float) =
        rotateAroundRad(originX, originY, angle * MathUtils.degreesToRadians)

fun Rectangle.rotateAroundRad(originX: Float, originY: Float, angleRad: Float) =
        setPosition(x - originX, y - originY).rotateRad(angleRad).setPosition(x + originX, y + originY)!!

fun Rectangle.rotateAround(origin: Vector2, angle: Float) =
        rotateAround(origin.x, origin.y, angle)

fun Rectangle.rotateAroundRad(origin: Vector2, angleRad: Float) =
        rotateAroundRad(origin.x, origin.y, angleRad)

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
