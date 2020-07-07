package com.company.todd.util.box2d

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.company.todd.objects.base.toMeters

object BodyFactory {
    const val DEFAULT_DENSITY = 1f
    const val DEFAULT_FRICTION = 0.2f
    const val DEFAULT_RESTITUTION = 0f
    const val DEFAULT_LINEAR_DAMPING = 0.5f

    val bodyDef = BodyDef()
    val fixtureDef = FixtureDef()

    fun createBody(world: World, type: BodyDef.BodyType, center: Vector2,
                   fixedRotation: Boolean = true, isBullet: Boolean = false) =
            world.createBody(bodyDef.apply {
                this.type = type
                this.position.set(center).toMeters()
                this.fixedRotation = fixedRotation
                this.bullet = isBullet
                this.linearDamping = DEFAULT_LINEAR_DAMPING
            })!!

    private fun createFixture(body: Body, shape_: Shape,
                              density_: Float, friction_: Float, restitution_: Float) =
            body.createFixture(fixtureDef.apply {
                density = density_
                friction = friction_
                restitution = restitution_
                shape = shape_
            })!!

    fun addBox(body: Body, width: Float, height: Float, center: Vector2 = Vector2(),
               density: Float = DEFAULT_DENSITY, friction: Float = DEFAULT_FRICTION,
               restitution: Float = DEFAULT_RESTITUTION, angle: Float = 0f) {
        PolygonShape().apply {
            // TODO addPolygon() - smooth box
            setAsBox(width.toMeters() / 2, height.toMeters() / 2, center.cpy().toMeters(), angle)
            createFixture(body, this, density, friction, restitution)
            dispose()
        }
    }

    fun addPolygon(body: Body, vertices: FloatArray, density: Float = DEFAULT_DENSITY,
                   friction: Float = DEFAULT_FRICTION, restitution: Float = DEFAULT_RESTITUTION) {
        PolygonShape().apply {
            set(vertices.map { it.toMeters() }.toFloatArray())
            createFixture(body, this, density, friction, restitution)
            dispose()
        }
    }

    fun addCircle(body: Body, radius: Float, center: Vector2 = Vector2(), density: Float = DEFAULT_DENSITY,
                  friction: Float = DEFAULT_FRICTION, restitution: Float = DEFAULT_RESTITUTION) {
        CircleShape().apply {
            position = center
            position.toMeters()
            this.radius = radius.toMeters()

            createFixture(body, this, density, friction, restitution)
            dispose()
        }
    }

    fun addEdge(body: Body, x1: Float, y1: Float, x2: Float, y2: Float,
                density: Float = DEFAULT_DENSITY, friction: Float = DEFAULT_FRICTION,
                restitution: Float = DEFAULT_RESTITUTION) {
        EdgeShape().apply {
            set(x1.toMeters(), y1.toMeters(), x2.toMeters(), y2.toMeters())
            createFixture(body, this, density, friction, restitution)
            dispose()
        }
    }

    fun addChain(body: Body, vertices: FloatArray, density: Float = DEFAULT_DENSITY,
                 friction: Float = DEFAULT_FRICTION, restitution: Float = DEFAULT_RESTITUTION) {
        ChainShape().apply {
            createChain(vertices.map { it.toMeters() }.toFloatArray())
            createFixture(body, this, density, friction, restitution)
            dispose()
        }
    }

    fun addLoopChain(body: Body, vertices: FloatArray, density: Float = DEFAULT_DENSITY,
                     friction: Float = DEFAULT_FRICTION, restitution: Float = DEFAULT_RESTITUTION) {
        ChainShape().apply {
            createLoop(vertices.map { it.toMeters() }.toFloatArray())
            createFixture(body, this, density, friction, restitution)
            dispose()
        }
    }
}
