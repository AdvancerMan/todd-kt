package com.company.todd.objects.base

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.Shape.Type.*
import com.company.todd.screen.GameScreen
import com.company.todd.util.box2d.BodyPattern

const val pixInMeter = 30f

fun Float.toPix() = this * pixInMeter
fun Float.toMeters() = this / pixInMeter

fun Vector2.toPix() = this.scl(pixInMeter)!!
fun Vector2.toMeters() = this.scl(1 / pixInMeter)!!

fun Rectangle.toPix() = this.set(x.toPix(), y.toPix(), width.toPix(), height.toPix())!!
fun Rectangle.toMeters() = this.set(x.toMeters(), y.toMeters(), width.toMeters(), height.toMeters())!!

fun Matrix4.toPix() = this.scl(pixInMeter)!!

class RealBodyWrapper(private val bodyPattern: BodyPattern): BodyWrapper {
    private lateinit var body: Body

    override fun init(gameScreen: GameScreen) {
        body = bodyPattern.let {
            it.world = gameScreen.world
            it.createBody()
        }
    }

    override fun applyLinearImpulseToCenter(impulse: Vector2) {
        body.applyLinearImpulse(impulse.cpy().toMeters(), body.worldCenter, true)
    }

    override fun getPosition() = body.position.cpy().toPix()

    override fun getVelocity() = body.linearVelocity.cpy().toPix()

    override fun setVelocity(v: Vector2) {
        body.linearVelocity = v.toMeters()
    }

    override fun setCenterPosition(x: Float, y: Float, resetLinearVelocity: Boolean) {
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

    override fun getAABB() =
            Rectangle().apply {
                val tmp = Vector2()
                body.fixtureList.forEach { merge(tmp, it.shape) }
                setCenter(body.position).toPix()
            }

    override fun destroy(world: World) {
        world.destroyBody(body)
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
